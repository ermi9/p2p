package com.ermiyas.exchange.domain.model;

/**
 * Enumeration of match results.
 * Used by the Event to declare the result and the Offer to track the prediction.
 */
public enum Outcome {
    HOME_WIN,
    AWAY_WIN,
    DRAW,
    PENDING
}