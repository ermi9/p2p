package com.ermiyas.exchange.application.offer;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.application.ports.OfferRepository;
import java.util.Objects;

/**
 * Application use case: creates an Offer and persists it via port.
 **/
public final class CreateOfferUseCase{
    private final OfferRepository offerRepository;
    public CreateOfferUseCase(OfferRepository offerRepository){
        this.offerRepository=Objects.requireNonNull(offerRepository);
    }
    public Offer execute(Money stake, Odds odds){
        Objects.requireNonNull(stake,"stake");
        Objects.requireNonNull(odds,"odds");
        Offer offer=Offer.create(stake, odds);
        offerRepository.save(offer);
        return offer;
    }
}