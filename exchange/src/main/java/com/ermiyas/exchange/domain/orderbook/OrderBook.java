package com.ermiyas.exchange.domain.orderbook;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.OfferId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class OrderBook {

    private final List<BetFillAgreement> agreements = new ArrayList<>();

    /**
     * A taker attempts to fill an offer.
     * Many takers may attempt until the offer is exhausted.
     */
    public BetFillAgreement fillOffer(Offer offer, Money liability, long takerUserId, String reference) {
        Objects.requireNonNull(offer, "offer");
        Objects.requireNonNull(liability, "liability");
        Objects.requireNonNull(reference, "reference");

        if (offer.status().name().contains("CANCELLED") || offer.status().name().contains("SETTLED")) {
            throw new OrderBookException("Offer is not active or fillable.");
        }

        // Perform the fill inside the Offer aggregate
        BetFillAgreement agreement = offer.fill(liability, takerUserId, reference);

        agreements.add(agreement);
        return agreement;
    }

    public List<BetFillAgreement> agreements() {
        return Collections.unmodifiableList(agreements);
    }

    /**
     * View remaining exposure for an offer.
     */
    public Money remainingExposure(Offer offer) {
        Objects.requireNonNull(offer, "offer");
        return offer.remainingStake();
    }

    public boolean isExhausted(Offer offer) {
        return offer.remainingStake().value().signum() == 0;
    }

    /**
     * Get all fill agreements for a specific offer.
     */
    public List<BetFillAgreement> agreementsForOffer(OfferId offerId) {
        Objects.requireNonNull(offerId, "offerId");
        return agreements.stream()
                .filter(a -> a.offerId().equals(offerId))
                .toList();
    }
}
