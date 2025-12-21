package com.ermiyas.exchange.domain.orderbook;

public  final class InvalidMatchAmountException extends OrderBookException {
    public InvalidMatchAmountException(){
        super("Match amount must be positive and not exceed remaining stake");
    }
}
