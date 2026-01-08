package com.ermiyas.exchange.domain.exception;

public class UserNotFoundException extends ExchangeException {
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND_002");
    }
}