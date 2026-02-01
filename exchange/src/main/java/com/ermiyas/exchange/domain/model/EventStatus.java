package com.ermiyas.exchange.domain.model;

/**
 * Tracks the lifecycle of a fixture.
 */
public enum EventStatus {
    OPEN,       // Betting is active
    COMPLETED,  // Match ended, result is known but funds not yet moved
    SETTLED     // Admin has triggered payouts and funds are released
}