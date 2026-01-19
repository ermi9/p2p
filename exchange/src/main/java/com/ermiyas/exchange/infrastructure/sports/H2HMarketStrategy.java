package com.ermiyas.exchange.infrastructure.sports;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;

import lombok.RequiredArgsConstructor;
import lombok.Value;
/*
*  We use a median-based filtering strategy because external Sports APIs 
 * occasionally return "junk" data—such as placeholder odds (e.g., 980.00)—
 * when a specific bookmaker market is suspended or malformed.
*/
@Component
@RequiredArgsConstructor 
public class H2HMarketStrategy implements TheOddsApiClient.MarketStrategy {

    private final FixtureNameMatcher nameMatcher;
    private static final double OUTLIER_THRESHOLD = 3.0; // 3x the median is considered junk

    @Override
    public boolean supports(MarketType type) {
        return type == MarketType.THREE_WAY;
    }

    @Override
    public String getMarketKey() {
        return "h2h";
    }

    @Override
    public BestOddsResult calculateBestOddsWithSources(TheOddsApiOddsDto dto) {
        // Pools to store all prices for consensus checking
        List<PricePoint> homePool = new ArrayList<>();
        List<PricePoint> awayPool = new ArrayList<>();
        List<PricePoint> drawPool = new ArrayList<>();

        List<TheOddsApiOddsDto.Bookmaker> bookmakers = dto.getBookmakers();
        if (bookmakers == null) return null;

        // 1. Collect all prices from all bookmakers
        for (int i = 0; i < bookmakers.size(); i++) {
            TheOddsApiOddsDto.Bookmaker bm = bookmakers.get(i);
            String providerTitle = (bm.getTitle() != null) ? bm.getTitle() : "Unknown";
            
            for (TheOddsApiOddsDto.Market mkt : bm.getMarkets()) {
                for (TheOddsApiOddsDto.Outcome outcome : mkt.getOutcomes()) {
                    double price = outcome.getPrice();
                    
                    if (nameMatcher.namesMatch(outcome.getName(), dto.getHomeTeam())) {
                        homePool.add(new PricePoint(price, providerTitle));
                    } else if (nameMatcher.namesMatch(outcome.getName(), dto.getAwayTeam())) {
                        awayPool.add(new PricePoint(price, providerTitle));
                    } else if (outcome.getName() != null && outcome.getName().equalsIgnoreCase("Draw")) {
                        drawPool.add(new PricePoint(price, providerTitle));
                    }
                }
            }
        }

        // 2. Filter outliers and find the best realistic price for each
        PricePoint bestHome = findBestRealistic(homePool);
        PricePoint bestAway = findBestRealistic(awayPool);
        PricePoint bestDraw = findBestRealistic(drawPool);

        return new BestOddsResult(
            Odds.of(bestHome.getPrice()), bestHome.getSource(),
            Odds.of(bestAway.getPrice()), bestAway.getSource(),
            Odds.of(bestDraw.getPrice()), bestDraw.getSource()
        );
    }

    /**
     *  Calculates median, removes outliers, and returns the best remaining price.
     */
    private PricePoint findBestRealistic(List<PricePoint> pool) {
        if (pool.isEmpty()) return new PricePoint(0.0, "N/A");
        if (pool.size() == 1) return pool.get(0);

        // Sort pool by price to calculate median
        pool.sort(Comparator.comparingDouble(PricePoint::getPrice));
        
        double median;
        int size = pool.size();
        if (size % 2 == 0) {
            median = (pool.get(size / 2 - 1).getPrice() + pool.get(size / 2).getPrice()) / 2.0;
        } else {
            median = pool.get(size / 2).getPrice();
        }

        // Filter out junk values (e.g. 980.0) that are way above median
        PricePoint best = new PricePoint(0.0, "N/A");
        for (int i = 0; i < pool.size(); i++) {
            PricePoint p = pool.get(i);
            // If the price is within a realistic range of the median
            if (p.getPrice() <= median * OUTLIER_THRESHOLD) {
                // Keep track of the highest valid price
                if (p.getPrice() > best.getPrice()) {
                    best = p;
                }
            }
        }
        return best;
    }

    @Override
    public List<Odds> calculateBestOdds(TheOddsApiOddsDto dto) {
        BestOddsResult result = calculateBestOddsWithSources(dto);
        List<Odds> OddsList = new ArrayList<>();
        OddsList.add(result.getHomeOdds());
        OddsList.add(result.getAwayOdds());
        OddsList.add(result.getDrawOdds());
        return OddsList;
    }

    @Value
    private static class PricePoint {
        double price;
        String source;
    }
}