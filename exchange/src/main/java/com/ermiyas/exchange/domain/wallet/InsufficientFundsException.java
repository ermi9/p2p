package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.common.Money;

/**
 * Provides a descriptive runtime error instead of bare IllegalArgumentException.
 */
public final class InsufficientFundsException extends WalletException {
    private final Money available;
    private final Money required;

    public InsufficientFundsException(Money available, Money required) {
        super(
                String.format(
                        "Insufficient funds: available=%s required=%s",
                        available.value(),
                        required.value()
                )
        );
        this.available = available;
        this.required = required;
    }

    public Money available() {
        return available;
    }

    public Money required() {
        return required;
    }
}
