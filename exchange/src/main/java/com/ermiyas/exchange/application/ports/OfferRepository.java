package com.ermiyas.exchange.application.ports;

import com.ermiyas.exchange.domain.offer.Offer;

import java.util.Optional;

public interface OfferRepository {

    Optional<Offer> findById(long offerId);

    Offer save(Offer offer);
}
