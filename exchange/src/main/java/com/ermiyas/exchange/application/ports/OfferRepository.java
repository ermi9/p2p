package com.ermiyas.exchange.application.ports;

import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.OfferId;

public interface OfferRepository{
    void save(Offer offer);
    Offer findById(OfferId offerId);
    void deleteById(OfferId offerId);
}