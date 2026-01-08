package com.ermiyas.exchange.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;


@Embeddable
public final class Odds {

    @Column(name = "value")
    private final BigDecimal value; 


    protected Odds() { this.value = BigDecimal.valueOf(2.0); }

    private Odds(BigDecimal value) {
        validate(value);
        this.value = value;
    }

    public static Odds of(BigDecimal value) { return new Odds(value); }
    
    public static Odds of(double value) { return new Odds(BigDecimal.valueOf(value)); }
    public static Odds of(String value) { return new Odds(new BigDecimal(value)); }



    public Money calculateLiability(Money stake) {
        return stake.multiply(profitMultiplier());
    }


    public BigDecimal calculateLiability(BigDecimal stakeAmount) {
        return stakeAmount.multiply(profitMultiplier());
    }


    private BigDecimal profitMultiplier() {
        return value.subtract(BigDecimal.ONE);
    }

    private void validate(BigDecimal value) {
        Objects.requireNonNull(value, "Odds value cannot be null");
        if (value.compareTo(BigDecimal.ONE) <= 0) {
            throw new IllegalArgumentException("Odds must be greater than 1.0");
        }
    }

    @JsonValue
    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Odds)) return false;
        Odds odds = (Odds) o;
        return value.compareTo(odds.value) == 0;
    }

    @Override
    public int hashCode() { return value.stripTrailingZeros().hashCode(); }

    @Override
    public String toString() { return value.toString(); }
}