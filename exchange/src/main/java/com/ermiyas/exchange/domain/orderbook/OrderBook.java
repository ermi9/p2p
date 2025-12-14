package com.ermiyas.exchange.domain.orderbook;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Offer;

import java.util.ArrayList;
import java.util.List;

public class OrderBook{
    private final long outcomeId;
    private final List<Offer> offers;

    public OrderBook(long outcomeId){
        this.outcomeId=outcomeId;
        this.offers=new ArrayList<>();
    }
    public void addOffer(Offer offer){

    }
    public MatchResult match(Money takerAmount){

    }
    public List<Offer> getOpenOffers(){

    }
    public long outcomeId(){
        return outcomeId;
    }
}