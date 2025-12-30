package com.ermiyas.exchange.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
public final class Money implements Comparable<Money> {
    
    @Column(name = "value")
    private BigDecimal value;

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    protected Money() {}

    public Money(BigDecimal value) {
        this.value = Objects.requireNonNull(value, "Money value cannot be null")
                            .setScale(SCALE, ROUNDING);
        if (this.value.signum() < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
    }

    /**
     * @JsonValue tells Jackson to serialize this object 
     * as a simple number in the JSON response.
     */
    @JsonValue
    public BigDecimal value() {
        return value;
    }

    public static Money zero() { return new Money(BigDecimal.ZERO); }
    public static Money of(BigDecimal value) { return new Money(value); }
    public static Money of(String value) { return new Money(new BigDecimal(value)); }
    
    public Money plus(Money other) { return new Money(this.value.add(other.value)); }
    public Money minus(Money other) { return new Money(this.value.subtract(other.value)); }
    public Money multiply(BigDecimal factor) { return new Money(this.value.multiply(factor)); }

    @Override
    public int compareTo(Money other) { return this.value.compareTo(other.value); }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return value.compareTo(money.value) == 0;
    }
    @Override
    public int hashCode() { return value.stripTrailingZeros().hashCode(); }
    @Override
    public String toString() { return value.toString(); }
}