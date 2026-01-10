package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor 
public class H2HMarketStrategy implements TheOddsApiClient.MarketStrategy {

    private final FixtureNameMatcher nameMatcher;

    @Override
    public boolean supports(MarketType type) {
        return type == MarketType.THREE_WAY;
    }

    @Override
    public String getMarketKey() {
        return "h2h";
    }

    /**
     * Standard Java implementation using traditional nested for-loops.
     */
    @Override
    public BestOddsResult calculateBestOddsWithSources(TheOddsApiOddsDto dto) {
        double bestHome = 0; String homeSource = "N/A";
        double bestAway = 0; String awaySource = "N/A";
        double bestDraw = 0; String drawSource = "N/A";

        List<TheOddsApiOddsDto.Bookmaker> bookmakers = dto.getBookmakers();
        if (bookmakers == null) {
            return null;
        }

        for (int i = 0; i < bookmakers.size(); i++) {
            TheOddsApiOddsDto.Bookmaker bm = bookmakers.get(i);
            List<TheOddsApiOddsDto.Market> markets = bm.getMarkets();
            if (markets == null) {
                continue;
            }

            for (int j = 0; j < markets.size(); j++) {
                TheOddsApiOddsDto.Market mkt = markets.get(j);
                List<TheOddsApiOddsDto.Outcome> outcomes = mkt.getOutcomes();
                if (outcomes == null) {
                    continue;
                }

                for (int k = 0; k < outcomes.size(); k++) {
                    TheOddsApiOddsDto.Outcome outcome = outcomes.get(k);
                    double currentPrice = outcome.getPrice();
                    String providerTitle = (bm.getTitle() != null) ? bm.getTitle() : "Unknown Provider";

                    // Use FUZZY MATCHING to resolve team name discrepancies
                    if (nameMatcher.namesMatch(outcome.getName(), dto.getHomeTeam())) {
                        if (currentPrice > bestHome) {
                            bestHome = currentPrice;
                            homeSource = providerTitle;
                        }
                    } else if (nameMatcher.namesMatch(outcome.getName(), dto.getAwayTeam())) {
                        if (currentPrice > bestAway) {
                            bestAway = currentPrice;
                            awaySource = providerTitle;
                        }
                    } else if (outcome.getName() != null && outcome.getName().equalsIgnoreCase("Draw")) {
                        if (currentPrice > bestDraw) {
                            bestDraw = currentPrice;
                            drawSource = providerTitle;
                        }
                    }
                }
            }
        }

        return new BestOddsResult(
            Odds.of(bestHome), homeSource,
            Odds.of(bestAway), awaySource,
            Odds.of(bestDraw), drawSource
        );
    }

    @Override
    public List<Odds> calculateBestOdds(TheOddsApiOddsDto dto) {
        BestOddsResult result = calculateBestOddsWithSources(dto);
        List<Odds> legacyList = new ArrayList<Odds>();
        legacyList.add(result.getHomeOdds());
        legacyList.add(result.getAwayOdds());
        legacyList.add(result.getDrawOdds());
        return legacyList;
    }
}