package com.ermiyas.exchange.domain.model;

/**
 * Tracks the lifecycle of an Offer in the market.
 */
public enum OfferStatus {
    OPEN,               // Initial state, no matches yet
    PARTIALLY_TAKEN,    // Some stake has been matched by Takers
    TAKEN,              // Stake is fully exhausted
    CANCELLED           // Maker withdrew the offer before it was fully matched
}