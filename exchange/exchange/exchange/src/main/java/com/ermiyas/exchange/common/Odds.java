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
    /**
     * Logic: A $100 stake at 4.0 odds creates a $300 liability for the Taker.
     * 
     */
    public Money calculateLiability(Money stake){
        return stake.multiply(this.profitMultiplier());
    }

    public BigDecimal minusOne() {
        return value.subtract(BigDecimal.ONE);
    }
    public static Odds of(String value){
        return new Odds(new BigDecimal(value));
    }
    @Override
    public boolean equals(Object o){
        if(this==o)
            return true;
        if (!(o instanceof Odds))
            return false;
        Odds odds=(Odds)o;
        return this.value.compareTo(odds.value)==0;
    }
}