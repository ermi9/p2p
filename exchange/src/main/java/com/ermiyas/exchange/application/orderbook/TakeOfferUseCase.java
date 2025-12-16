package com.ermiyas.exchange.application.orderbook;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.application.ports.OrderBookRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import com.ermiyas.exchange.domain.orderbook.OrderBook;
//orchestrates the scenario where the user takes the offer
public class TakeOfferUseCase {
    private final OrderBookRepository orderBookRepository;
    private final OfferRepository offerRepository;
    private final BetAgreementRepository betAgreementRepository;

    public TakeOfferUseCase(OrderBookRepository orderBookRepository,OfferRepository offerRepository,BetAgreementRepository betAgreementRepository){
        this.orderBookRepository=orderBookRepository;
        this.offerRepository=offerRepository;
        this.betAgreementRepository=betAgreementRepository;
    }

    public BetAgreement execute(long outcomeId,long offerId,long takerUserId,Money amount){
    
        OrderBook orderBook=orderBookRepository.findByOutcomeId(outcomeId);
        Offer offer=offerRepository.findById(offerId);
        BetAgreement agreement=orderBook.matchOffer(offer, takerUserId, amount);
    
        offerRepository.save(offer);
        betAgreementRepository.save(agreement);
        orderBookRepository.save(orderBook);

        return agreement;
    }
}
