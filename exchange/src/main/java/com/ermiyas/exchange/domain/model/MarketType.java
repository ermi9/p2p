package com.ermiyas.exchange.domain.model;

/**
 * PURE OOP: Domain Enum.
 * Represents the type of market, used by the Factory to pick 
 * the correct SettlementStrategy.
 */
public enum MarketType {
    THREE_WAY,
    //BTTS,
    //ASIAN_HANDICAP
}