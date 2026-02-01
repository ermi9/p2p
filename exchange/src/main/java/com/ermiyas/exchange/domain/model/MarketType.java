package com.ermiyas.exchange.domain.model;

/**
 * Represents the type of market, used by the Factory to pick 
 * the correct SettlementStrategy.
 */
public enum MarketType {
    THREE_WAY,
    //BTTS,
    //ASIAN_HANDICAP
}