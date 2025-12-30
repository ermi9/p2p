package com.ermiyas.exchange.domain.vo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * PURE OOP: Commission Policy Value Object.
 * Encapsulates the rules for platform fees. 
 */
public final class CommissionPolicy {
    private final BigDecimal rate; // e.g., 0.05 for 5%

    /**
     * Default constructor for standard 5% platform fee.
     */
    public CommissionPolicy() {
        this(new BigDecimal("0.05"));
    }

    public CommissionPolicy(BigDecimal rate) {
        this.rate = Objects.requireNonNull(rate, "Commission rate cannot be null");
        
        // Business Rule: Rate must be between 0 (0%) and 1 (100%)
        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalArgumentException("Commission rate must be between 0 and 1");
        }
    }

    /**
     * Static factory for a standard 5% policy.
     */
    public static CommissionPolicy defaultPolicy() {
        return new CommissionPolicy(new BigDecimal("0.05"));
    }

    /**
     * Calculates the house cut based on net profit.
     * Logic: Commission = Net Profit * Rate
     */
    public Money calculateCommission(Money netProfit) {
        Objects.requireNonNull(netProfit, "Net profit cannot be null");
        return netProfit.multiply(this.rate);
    }

    /**
     * Applies the policy to a net profit amount.
     * Logic: Return Amount = Net Profit - Commission
     */
    public Money apply(Money netProfit) {
        Money commission = calculateCommission(netProfit);
        return netProfit.minus(commission);
    }

    public BigDecimal rate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommissionPolicy)) return false;
        CommissionPolicy that = (CommissionPolicy) o;
        return rate.compareTo(that.rate) == 0;
    }

    @Override
    public int hashCode() {
        return rate.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return (rate.multiply(new BigDecimal("100"))) + "%";
    }
}