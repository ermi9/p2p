package com.ermiyas.exchange.domain.orderbook;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Position;

import java.util.Objects;
public final class BetAgreement{
    private final long offerId;
    private final long makerUserId;
    private final long takerUserId;
    private final long outcomeId;

    private final Position makerPosition;
    private final Odds odds;
    private final Money stake;

    //risk per side---to account for the scenario they might stake different risks
    
    private final Money makerRisk;
    private final Money takerRisk;

public BetAgreement(long offerId,long makerUserId,long takerUserId,long outcomeId,Position makerPosition,Odds odds, Money stake){
    this.offerId=offerId;
    this.makerUserId=makerUserId;
    this.takerUserId=takerUserId;
    this.outcomeId=outcomeId;
    this.makerPosition=Objects.requireNonNull(makerPosition);
    this.odds=Objects.requireNonNull(odds);
    this.stake=Objects.requireNonNull(stake);
//risk calculation ONCE
    if(makerPosition==Position.FOR){
        this.makerRisk=stake;
        this.takerRisk=stake.multiply(odds.minusOne());
    }
    else{
        this.makerRisk=stake.multiply(odds.minusOne());
        this.takerRisk=stake;
    }
}

public long winnerUserId(boolean outcomeHappened){
    boolean makerWins=(makerPosition==Position.FOR &&outcomeHappened) || (makerPosition == Position.AGAINST &&!outcomeHappened);
    return makerWins ? makerUserId : takerUserId;
}

public long loserUserId(boolean outcomeHappened){
    return winnerUserId(outcomeHappened)==makerUserId ? takerUserId : makerUserId;
}


//getters
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
public Position makerPosition(){
    return makerPosition;
}
public Odds odds(){
    return odds;

}
public Money stake(){
    return stake;
}
public Money makerRisk(){
    return makerRisk;
}
public Money takerRisk(){
    return takerRisk;
}
public Money totalPayout(){
    return makerRisk.plus(takerRisk);
}

}
