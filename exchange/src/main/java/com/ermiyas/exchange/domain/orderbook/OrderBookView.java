package com.ermiyas.exchange.domain.orderbook;
import com.ermiyas.exchange.domain.offer.Offer;
import java.util.Collections;
import java.util.List;
public final class OrderBookView {
    private final long outcomeId;
    private final List<Offer> forOffers;
    private final List<Offer> againstOffers;
    public OrderBookView(long outcomeId,List<Offer> forOffers,List<Offer> againstOffers){
        this.outcomeId=outcomeId;
        this.forOffers=List.copyOf(forOffers);
        this.againstOffers=List.copyOf(againstOffers);
    }
    public long outcomeId(){
        return outcomeId;
    }
    public List<Offer> forOffers(){
        return Collections.unmodifiableList(forOffers);
    }
    public List<Offer> againstOffers(){
        return Collections.unmodifiableList(againstOffers);
    }

}
