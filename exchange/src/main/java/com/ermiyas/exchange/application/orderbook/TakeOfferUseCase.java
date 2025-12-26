package com.ermiyas.exchange.application.orderbook;

import com.ermiyas.exchange.common.Money;

import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.OfferId;
import com.ermiyas.exchange.domain.orderbook.BetFillAgreement;
import com.ermiyas.exchange.domain.orderbook.OrderBook;
import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.application.ports.BetAgreementRepository;

import java.util.Objects;
/*Application use case: a Taker fills an existing Offer. */

public final class TakeOfferUseCase{
    private final OfferRepository offerRepository;
    private final BetAgreementRepository betAgreementRepository;
    private final OrderBook orderBook;
    public TakeOfferUseCase(OfferRepository offerRepository,BetAgreementRepository betAgreementRepository,OrderBook orderBook){
        this.offerRepository=Objects.requireNonNull(offerRepository,"offerRepository");
        this.orderBook=Objects.requireNonNull(orderBook,"orderBook");
        this.betAgreementRepository=Objects.requireNonNull(betAgreementRepository);

    }
    public BetFillAgreement execute(OfferId offerId,Money liability,long takerUserId,String reference){
        Objects.requireNonNull(offerId,"offerId");
        Objects.requireNonNull(liability,"liability");
        Objects.requireNonNull(reference,"reference");

        Offer offer=offerRepository.findById(offerId);
        if(offer==null)
            throw new IllegalStateException("Offer not found!");
        BetFillAgreement agreement=orderBook.fillOffer(offer, liability, takerUserId, reference);
        betAgreementRepository.save(agreement);
        return agreement;
    }

}
