package com.ermiyas.exchange.domain.orderbook;

public final class WrongOutcomeException extends OrderBookException {
    public WrongOutcomeException(){
        super("Offer does not belong to this outcome");
    }
}
