package com.ermiyas.exchange.domain.logic;

import com.ermiyas.exchange.domain.model.Outcome;
import com.ermiyas.exchange.domain.model.MarketType; // 

/**
   Strategy Interface.
 * Now it includes getMarketType() so the Factory can find it.
 */
public interface SettlementStrategy {
    
    Outcome determineWinner(int homeScore, int awayScore);

    // Added: This is the "ID card" for the strategy
    // so that the we know in which market we are. NOTE: market,event,outcome are different
    //Events are the matches we are betting on, markets are subdomains of the events and we are basically saying this specific thing in this event, the outcome is an outcome but in that specific market type we chose.
    //example: Events=Arsenal vs Manchester Unnited
              //Market=BTTS,H2H(Threeway), ASIAN_HANDICAP..
              //Outcome=Yes/No, HOME_WIN,AWAY_WIN,DRAW,...
    MarketType getMarketType();
}