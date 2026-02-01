package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Concrete implementation of a percentage-based fee.
 * Demonstrates Inclusion Polymorphism and Robust Validation.
 */
public final class StandardPercentagePolicy implements CommissionPolicy {
    private final BigDecimal rate;

    public StandardPercentagePolicy(BigDecimal rate) throws ExchangeException {
        validateRate(rate);
        this.rate = rate;
    }

    @Override
    public Money apply(Money netProfit) throws ExchangeException {
        Objects.requireNonNull(netProfit, "Net profit cannot be null");

        Money commission = netProfit.multiply(this.rate);
        return netProfit.minus(commission);
    }

    @Override
    public String getPolicyDescription() {
        return rate.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "% Standard Fee";
    }

    private void validateRate(BigDecimal rate) throws ExchangeException {
        if (rate == null || rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalBetException("Security Violation: Commission rate must be 0-1. Provided: " + rate);
        }
    }
}