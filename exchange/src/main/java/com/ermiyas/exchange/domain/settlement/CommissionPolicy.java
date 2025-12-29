package com.ermiyas.exchange.domain.settlement;
import com.ermiyas.exchange.common.Money;
import java.math.BigDecimal;
public final class CommissionPolicy {

    private final BigDecimal rate; // e.g. 0.02 = 2%

    public CommissionPolicy(double rate) {
        if (rate < 0 || rate > 0.1) {
            throw new IllegalArgumentException("Invalid commission rate");
        }
        this.rate = BigDecimal.valueOf(rate);
    }

    public Money calculate(Money profit) {
        return profit.multiply(rate);
    }
}

