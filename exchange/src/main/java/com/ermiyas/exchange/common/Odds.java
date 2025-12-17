package com.ermiyas.exchange.common;

import java.math.BigDecimal;
import java.util.Objects;

public final class Odds {
    private final BigDecimal value;
    
    public Odds(BigDecimal value) {
        this.value = Objects.requireNonNull(value, "value");
        if (value.compareTo(BigDecimal.ONE) <= 0)
            throw new IllegalArgumentException("Odds must be greater than 1");    
    }

    public BigDecimal value() {
        return value;
    }

    /**
     * Required by SettledBet for payout logic.
     * Returns (Odds - 1) as a BigDecimal.
     */
    public BigDecimal profitMultiplier() {
        return value.subtract(BigDecimal.ONE);
    }

    public BigDecimal minusOne() {
        return value.subtract(BigDecimal.ONE);
    }
}