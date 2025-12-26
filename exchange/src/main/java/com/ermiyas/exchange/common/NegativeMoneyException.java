package com.ermiyas.exchange.common;

public class NegativeMoneyException extends RuntimeException {
    public NegativeMoneyException(String message) {
        super(message);
    }
}
