package com.ermiyas.exchange.domain.exception;

public class SecurityViolationException extends ExchangeException {
    public SecurityViolationException(String message) {
        // We pass the message and a specific error code to the parent
        super(message, "AUTH_ERR_001");
    }
}