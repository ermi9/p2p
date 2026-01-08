package com.ermiyas.exchange.domain.exception;

public class IllegalBetException extends ExchangeException {
    public IllegalBetException(String message) {
        super(message, "BET_ERR_001");
    }
    public IllegalBetException(String message, String errorCode){
        super(message,errorCode);
    }
}