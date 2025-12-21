package com.ermiyas.exchange.domain.orderbook;

public abstract class OrderBookException extends RuntimeException {
    protected OrderBookException(String message){
        super(message);
    }
}
