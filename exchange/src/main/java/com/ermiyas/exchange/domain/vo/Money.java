package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;


@Embeddable
public final class Money implements Comparable<Money> {
    
    @Column(name = "value")
    private final BigDecimal value; //  

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;


    protected Money() { 
        this.value = BigDecimal.ZERO.setScale(SCALE, ROUNDING); 
    }


    private Money(BigDecimal value) throws IllegalBetException {
        Objects.requireNonNull(value, "Money value cannot be null");
        BigDecimal scaledValue = value.setScale(SCALE, ROUNDING);
        
        if (scaledValue.signum() < 0) {
            throw new IllegalBetException("Financial Integrity Violation: Money cannot be negative. Value: " + scaledValue);
        }
        this.value = scaledValue;
    }

    
    public static Money zero() {
        try {
            return new Money(BigDecimal.ZERO);
        } catch (IllegalBetException e) {
            throw new RuntimeException("System Integrity Error: Zero cannot be negative");
        }
    }
    @JsonCreator
    public static Money of(BigDecimal value) throws IllegalBetException { 
        return new Money(value); 
    }

    public static Money of(String value) throws IllegalBetException { 
        return new Money(new BigDecimal(value)); 
    }


    public Money plus(Money other) { 
        try {
            return new Money(this.value.add(other.value)); 
        } catch (IllegalBetException e) {
            throw new IllegalStateException("Addition should not result in negative money", e);
        }
    }

    public Money plus(BigDecimal amount) throws IllegalBetException {
        return new Money(this.value.add(amount));
    }

    public Money plus(double amount) throws IllegalBetException {
        return new Money(this.value.add(BigDecimal.valueOf(amount)));
    }

    public Money minus(Money other) throws IllegalBetException { 
        return new Money(this.value.subtract(other.value)); 
    }


    public boolean isGreaterThan(Money other) {
        return this.compareTo(other) > 0;
    }

    public boolean isZero() {
        return this.value.signum() == 0;
    }

    public boolean isNegative() {
        return this.value.signum() < 0;
    }

    @JsonValue
    public BigDecimal value() {
        return value;
    }

    public Money multiply(BigDecimal factor) { 
        try {
            return new Money(this.value.multiply(factor)); 
        } catch (IllegalBetException e) {
            throw new IllegalStateException("Multiplication resulted in negative money", e);
        }
    }

    @Override
    public int compareTo(Money other) { 
        return this.value.compareTo(other.value); 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return value.compareTo(money.value) == 0;
    }

    @Override
    public int hashCode() { 
        return value.stripTrailingZeros().hashCode(); 
    }
    
    @Override
    public String toString() { 
        return value.toString(); 
    }
}