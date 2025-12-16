package com.ermiyas.exchange.domain.orderbook;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.OfferStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Wired up the previously empty order book so matching/settlement can work.
 */
public class OrderBook{
    private final long outcomeId;
    private final List<Offer> offers;

    public OrderBook(long outcomeId){
        this.outcomeId=outcomeId;
        this.offers=new ArrayList<>();
    }

    public void addOffer(Offer offer){
        if(offer.outcomeId()!=outcomeId){
            throw new WrongOutcomeException();
        }
        offers.add(offer);
    }

    public BetAgreement matchOffer(Offer offer,long takerUserId,Money amount){
        if(offer.outcomeId()!=outcomeId){
            throw new WrongOutcomeException();
        }
        if(amount==null || amount.value().signum()<=0){
            throw new InvalidMatchAmountException();
        }
        if(offer.remainingStake().value().compareTo(amount.value())<0){
            throw new InvalidMatchAmountException();
        }
        if(offer.status()==OfferStatus.FILLED){
            throw new OfferNotOpenException();
        }

        offer.consume(amount);
        removeFilledOffers();

        return new BetAgreement(
                offer.id(),
                offer.makerUserId(),
                takerUserId,
                outcomeId,
                offer.position(),
                offer.odds(),
                amount
        );
    }

    public List<Offer> getOpenOffers(){
        return Collections.unmodifiableList(offers);
    }

    public long outcomeId(){
        return outcomeId;
    }

    private void removeFilledOffers(){
        Iterator<Offer> iterator=offers.iterator();
        while(iterator.hasNext()){
            if(iterator.next().isFilled()){
                iterator.remove();
            }
        }
    }
}
