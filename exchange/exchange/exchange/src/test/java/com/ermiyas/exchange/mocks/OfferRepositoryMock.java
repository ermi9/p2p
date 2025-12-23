package com.ermiyas.exchange.mocks;

import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import java.util.HashMap;
import java.util.Map;

public class OfferRepositoryMock implements OfferRepository {
    private final Map<Long, Offer> store = new HashMap<>();
    private long idCounter = 1;

@Override
public Offer save(Offer offer) {
    long id = (offer.id() == 0) ? idCounter++ : offer.id();
    
    // Using the getter we just added
    Offer saved = new Offer(
        id, 
        offer.makerUserId(), 
        offer.outcomeId(), 
        offer.odds(), 
        offer.initialStake() 
    );
    
    // Sync the remaining stake if it was consumed
    if (offer.remainingStake().compareTo(offer.initialStake()) < 0) {
        Money amountConsumed = offer.initialStake().minus(offer.remainingStake());
        saved.consume(amountConsumed);
    }

    store.put(id, saved);
    return saved;
}

    @Override
    public Offer findById(long id) {
        return store.get(id);
    }
}