package com.ermiyas.exchange.application.offer;

import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.application.ports.OrderBookRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.Position;
import com.ermiyas.exchange.domain.orderbook.OrderBook;
public class CreateOfferUseCase {
    private final OfferRepository offerRepository;
    private final OrderBookRepository orderBookRepository;

    public CreateOfferUseCase(
        OfferRepository offerRepository,OrderBookRepository orderBookRepository){
            this.offerRepository=offerRepository;
            this.orderBookRepository=orderBookRepository;
        }
    public void execute(long offerId,long makerUserId,long outcomeId, Position position,Odds odds,Money stake){
        Offer offer =new Offer(offerId,makerUserId,outcomeId,position,odds,stake);
        OrderBook orderBook= orderBookRepository.findByOutcomeId(outcomeId);
        orderBook.addOffer(offer);
        offerRepository.save(offer);
        orderBookRepository.save(orderBook);
    }
    
}
