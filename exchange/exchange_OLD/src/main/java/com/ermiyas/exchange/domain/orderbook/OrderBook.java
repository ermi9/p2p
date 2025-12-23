package com.ermiyas.exchange.domain.orderbook;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderBook {
    private final long outcomeId;
    private final List<Offer> offers;

    public OrderBook(long outcomeId) {
        this.outcomeId = outcomeId;
        this.offers = new ArrayList<>();
    }

    public void addOffer(Offer offer) {
        if (offer.outcomeId() != outcomeId) {
            throw new WrongOutcomeException();
        }
        offers.add(offer);
    }

    public BetAgreement matchOffer(Offer offer, long takerUserId, Money amount) {
        // Basic validation
        if (offer.outcomeId() != outcomeId) {
            throw new WrongOutcomeException();
        }
        if (amount == null || amount.value().signum() <= 0) {
            throw new InvalidMatchAmountException();
        }

        // 1. Update the offer stake
        offer.consume(amount);
        
        // 2. Simple removal: Remove the offer from the list if it is fully matched (filled)--recheck later on Main
        if (offer.isFilled()) {
            offers.remove(offer);
        }

        // 3. Create the agreement (FIXED: Added offer.id() as the first argument)--recheck later on Main
        return new BetAgreement(
    offer.id(),
    offer.makerUserId(), // Changed from userId() to makerUserId()--recheck later on Main
    takerUserId,
    this.outcomeId,
    offer.odds(),
    amount
);
    }

    public List<Offer> getOpenOffers() {
        return Collections.unmodifiableList(offers);
    }

    public long outcomeId() {
        return outcomeId;
    }
}
