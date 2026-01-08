package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * REFACTORED: H2HMarketStrategy (OCP Friendly)
 */
@Component
public class H2HMarketStrategy implements TheOddsApiClient.MarketStrategy {

    @Override
    public boolean supports(MarketType type) {
        return type == MarketType.THREE_WAY;
    }

    @Override
    public String getMarketKey() {
        return "h2h";
    }

    @Override
    public List<Odds> calculateBestOdds(TheOddsApiOddsDto dto) {
        double bestHome = 0;
        double bestAway = 0;
        double bestDraw = 0;

        List<TheOddsApiOddsDto.Bookmaker> bookmakers = dto.getBookmakers();
        if (bookmakers != null) {
            for (int i = 0; i < bookmakers.size(); i++) {
                TheOddsApiOddsDto.Bookmaker bm = bookmakers.get(i);
                List<TheOddsApiOddsDto.Market> markets = bm.getMarkets();
                
                if (markets != null) {
                    for (int j = 0; j < markets.size(); j++) {
                        TheOddsApiOddsDto.Market mkt = markets.get(j);
                        List<TheOddsApiOddsDto.Outcome> outcomes = mkt.getOutcomes();
                        
                        if (outcomes != null) {
                            for (int k = 0; k < outcomes.size(); k++) {
                                TheOddsApiOddsDto.Outcome outcome = outcomes.get(k);
                                
                                if (outcome.getName().equals(dto.getHomeTeam())) {
                                    bestHome = Math.max(bestHome, outcome.getPrice());
                                } else if (outcome.getName().equals(dto.getAwayTeam())) {
                                    bestAway = Math.max(bestAway, outcome.getPrice());
                                } else {
                                    bestDraw = Math.max(bestDraw, outcome.getPrice());
                                }
                            }
                        }
                    }
                }
            }
        }

        List<Odds> result = new ArrayList<>();
        result.add(Odds.of(bestHome));
        result.add(Odds.of(bestAway));
        result.add(Odds.of(bestDraw));
        
        return result;
    }
}