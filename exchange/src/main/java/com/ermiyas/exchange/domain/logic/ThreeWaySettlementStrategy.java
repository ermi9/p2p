package com.ermiyas.exchange.domain.logic;

import com.ermiyas.exchange.domain.model.Outcome;
import com.ermiyas.exchange.domain.model.MarketType;
import org.springframework.stereotype.Component;

/**
   Concrete Strategy for Home/Away/Draw markets.
 * It now explicitly tells the system it handles THREE_WAY markets.
 */
@Component
public class ThreeWaySettlementStrategy implements SettlementStrategy {

    @Override
    public Outcome determineWinner(int homeScore, int awayScore) {
        if (homeScore > awayScore) {
            return Outcome.HOME_WIN;
        } else if (awayScore > homeScore) {
            return Outcome.AWAY_WIN;
        } else {
            return Outcome.DRAW;
        }
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.THREE_WAY;
    }
}