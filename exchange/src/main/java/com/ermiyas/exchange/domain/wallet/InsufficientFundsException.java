package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.common.Money;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Money balance, Money attempted) {
        super("Insufficient funds: balance=" + balance + ", attempted=" + attempted);
    }
}
