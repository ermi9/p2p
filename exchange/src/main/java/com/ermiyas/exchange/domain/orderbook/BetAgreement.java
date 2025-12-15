package com.ermiyas.exchange.domain.orderbook;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Position;

public final class BetAgreement{
    private final long offerId;
    private final long makerUserId;
    private final long takerUserId;
    private final long outcomeId;

    private final Position position;
    private final Odds odds;
    private final Money amount;
    
public BetAgreement(long offerId,long makerUserId,long takerUserId,long outcomeId,Position position,Odds odds, Money amount){
    this.offerId=offerId;
    this.makerUserId=makerUserId;
    this.takerUserId=takerUserId;
    this.outcomeId=outcomeId;
    this.position=position;
    this.odds=odds;
    this.amount=amount;
}
public long offerId(){
    return offerId;
}
public long makerUserId(){
    return makerUserId;
}
public long takerUserId(){
    return takerUserId;
}
public long outcomeId(){
    return outcomeId;
}
public Position position(){
    return position;
}
public Odds odds(){
    return odds;

}
public Money amount(){
    return amount;
}
}
