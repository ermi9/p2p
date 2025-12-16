package com.ermiyas.exchange.application.ports;

import com.ermiyas.exchange.domain.offer.Offer;


public interface OfferRepository {

    Offer findById(long offerId);

    Offer save(Offer offer);
}
