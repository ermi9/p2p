package com.ermiyas.exchange.domain.offer;

/**
 * Basic two-sided market positions.
 * Added so offers/order books can reason about which side owns the exposure.
 */
public enum Position {
    FOR,
    AGAINST;

    public Position opposite() {
        return this == FOR ? AGAINST : FOR;
    }
}
