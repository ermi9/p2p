package com.ermiyas.exchange.domain.model;

/**
 * PURE OOP: Tracks whether a matched bet has been paid out.
 */
public enum BetStatus {
    MATCHED, // Active contract
    SETTLED  // Funds have been moved to the winner's wallet
}