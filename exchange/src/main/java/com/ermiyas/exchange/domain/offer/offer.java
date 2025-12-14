package com.ermiyas.exchange.domain.offer;
import com.ermiyas.exchange.domain.common.Money;
import com.ermiyas.exchange.domain.common.Odds;
import java.util.Objects;
import java.time.Instant;

public class offer{
    private final long  id;
    private final long  makerUserId;
    private final long  outcomeId;
    private final Odds odds;
    private final Money inititalStake;
    private Money remainingStake;
    private final Instant createdAt;

    public offer(long id, long makerUserId,long outcomeId,Odds odds,Money stake)
{
    if(stake.value().signum()<=0) throw new IllegalArgumentException("Stake must be greater than 0!");
    this.id=id;
    this.makerUserId=makerUserId;
    this.outcomeId=outcomeId;
    this.Odds= odds;
    this.initialStake=initialStake;
    this.remainingStake=remainingStake;
    this.createdAt=createdAt;


}
public void consume(Money amount){
    if (amount.value.signum()<=0) 
        throw new IllegalArgumentException("Stake must be greater than 0!");

    if(amount.value().compareTo(remainingStake.value()>0) 
        throw new IllegalStateException("Not enough remaining stake!");
    
    remainingStake=remainingStake.minus(amount);
}
public OfferStatus status(){
    if(isFilled()) return OfferStatus.FILLED;
    if (remainingStake.value().compareTo(initialStake.value())<0;) return OfferStatus.PARTIALLY_FILLED;
}

public boolean isFilled(){
    //only true if remainingStake==0 otherwise false 

    return remainingStake.value().signum()==0;
}
public long id(){return id;}
public long makerUserId(){return makerUserId;}

public long outcomeId(){return outcomeId;}
public Odds odds(){return odds;}
public  Money remainingStake(){return remainingStake;}
public Instant createdAt(){return createdAt;}


@Override
public boolean equals(Object o){
    if(this==o) return true;
    if(!(o instanceof offer)) return false;
    Offer offer=(Offer) o;
    return offerId==offer.offerId;
}
@Override
public int hashcode(){
    return Objects.hash(offerId);
}

    
}