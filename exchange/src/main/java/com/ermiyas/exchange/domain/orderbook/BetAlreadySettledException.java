package com.ermiyas.exchange.domain.orderbook;

public final class BetAlreadySettledException extends RuntimeException {
public BetAlreadySettledException(long offerId,long makerId,long takerId){
    super(String.format("Bet Agreement for Offer [%d] between Maker [%d] and Taker [%d] is already settled and cannot be modified",offerId,makerId,takerId));
}
    
}
