package com.ermiyas.exchange.domain.exception;


public abstract class ExchangeException extends Exception {
    private final String errorCode;

    protected ExchangeException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}