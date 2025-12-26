package com.ermiyas.exchange.domain.orderbook;

public class OfferNotOpenException extends RuntimeException{
    public OfferNotOpenException(String message){
        super(message);
    }
}