package com.ermiyas.exchange.domain.orderbook;

public final class OfferNotOpenException extends OrderBookException {
    public OfferNotOpenException(){
        super("Offer is not open for matching");
    }
    
}
