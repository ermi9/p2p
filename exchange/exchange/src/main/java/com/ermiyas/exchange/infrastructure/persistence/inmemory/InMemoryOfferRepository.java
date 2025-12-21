package com.ermiyas.exchange.infrastructure.persistence.inmemory;
import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.domain.offer.Offer;
import java.util.HashMap;
import java.util.Map;
public class InMemoryOfferRepository implements OfferRepository {
    private final Map<Long,Offer> OffersById=new HashMap<>();

    @Override
    public Offer findById(long offerId){
        Offer offer=OffersById.get(offerId);
        if(offer==null)
            throw new IllegalStateException("Offer not found "+offer);
        return offer;
    }
    @Override
    public Offer save(Offer offer){
        OffersById.put(offer.id(), offer);
        return offer;
    }
    
}
