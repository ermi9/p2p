package com.ermiyas.exchange.domain.offer;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import java.util.Objects;

import com.ermiyas.exchange.domain.offer.Position;
import java.time.Instant;

public class Offer{
    private final long  id;
    private final long  makerUserId;
    private final long  outcomeId;
    private final Position position;
    private final Odds odds;
    private final Money initialStake;
    private Money remainingStake;
    private final Instant createdAt;

    public Offer(long id, long makerUserId,long outcomeId,Position position,Odds odds,Money stake)
{
    if(stake == null ||stake.value().signum()<=0) throw new IllegalArgumentException("Stake must be greater than 0!");
    this.id=id;
    this.makerUserId=makerUserId;
    this.outcomeId=outcomeId;
    this.position=Objects.requireNonNull(position);
    this.odds= Objects.requireNonNull(odds);
    this.initialStake=Objects.requireNonNull(stake);
    this.remainingStake=Objects.requireNonNull(stake);
    this.createdAt=Instant.now();


}


public void consume(Money amount){
    if (amount==null || amount.value().signum()<=0) 
        throw new IllegalArgumentException("Stake must be greater than 0!");

    if(amount.value().compareTo(remainingStake.value())>0) 
        throw new IllegalStateException("Not enough remaining stake!");
    
    remainingStake=remainingStake.minus(amount);
}

public OfferStatus status(){
    if(isFilled()) 
        return OfferStatus.FILLED;
    //if the remainingStake !=0 AND less than initialStake--> PARTIALLY_FILLED
    if (remainingStake.value().compareTo(initialStake.value())<0) 
        return OfferStatus.PARTIALLY_FILLED;
return OfferStatus.OPEN;
}


public boolean isFilled(){
    //only true if remainingStake==0 otherwise false 

    return remainingStake.value().signum()==0;
}


public long id(){return id;}

public long makerUserId(){return makerUserId;}

public Position position(){return position;}

public long outcomeId(){return outcomeId;}

public Odds odds(){return odds;}

public  Money remainingStake(){return remainingStake;}

public Instant createdAt(){return createdAt;}

/* 
WILL BE CHECKED AND UNCOMMMENTED LATER ON...
@Override
public boolean equals(Object o){
    if(this==o) return true;
    if(!(o instanceof Offer)) return false;
    Offer offer=(Offer) o;
    return id==offer.id;
}
@Override
public int hashCode(){
    return Objects.hash(id);
}

   */ 
}