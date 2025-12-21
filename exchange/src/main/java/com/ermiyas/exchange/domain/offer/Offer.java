package com.ermiyas.exchange.domain.offer;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import java.util.Objects;
import com.ermiyas.exchange.domain.wallet.InsufficientFundsException;

import java.time.Instant;

public class Offer{
    private final long  id;
    private final long  makerUserId;
    private final long  outcomeId;
    private final Odds odds;
    private final Money initialStake;
    private Money remainingStake;
    private final Instant createdAt;

    public Offer(long id, long makerUserId,long outcomeId,Odds odds,Money stake)
{
    if(stake == null ||stake.compareTo(Money.zero())<=0) throw new IllegalArgumentException("Stake must be greater than 0!");
    this.id=id;
    this.makerUserId=makerUserId;
    this.outcomeId=outcomeId;
    this.odds= Objects.requireNonNull(odds);
    this.initialStake=Objects.requireNonNull(stake);
    this.remainingStake=Objects.requireNonNull(stake);
    this.createdAt=Instant.now();


}


public void consume(Money amount){
    if (amount.compareTo(Money.zero())<=0) 
        throw new IllegalArgumentException("Amount to consume must be positive");

    if(amount.compareTo(remainingStake)>0) 
        throw new InsufficientFundsException(remainingStake, amount);//reused the custom Exception I created
    
    remainingStake=this.remainingStake.minus(amount);
}

public OfferStatus status(){
    if(isFilled()) 
        return OfferStatus.FILLED;
    //if the remainingStake !=0 AND less than initialStake--> PARTIALLY_FILLED
    if (remainingStake.compareTo(initialStake) < 0) 
        return OfferStatus.PARTIALLY_FILLED;
return OfferStatus.OPEN;
}


public boolean isFilled(){
    //only true if remainingStake==0 otherwise false 
    return remainingStake.compareTo(Money.zero())==0;

}

//Getters
public long id(){return id;}
public long makerUserId(){return makerUserId;}
public long outcomeId(){return outcomeId;}
public Odds odds(){return odds;}
public  Money remainingStake(){return remainingStake;}
public Instant createdAt(){return createdAt;}
public Money initialStake(){return initialStake;}
}