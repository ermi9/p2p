package com.ermiyas.exchange.application.orderbook;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.application.ports.OrderBookRepository;
import com.ermiyas.exchange.application.ports.WalletRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.Position;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import com.ermiyas.exchange.domain.wallet.WalletService;

import java.util.Objects;
//orchestrates the scenario where the user takes the offer

public class TakeOfferUseCase {
    private final WalletService walletService;
    private final OfferRepository offerRepository;
    private final BetAgreementRepository betAgreementRepository;


    public TakeOfferUseCase(WalletService walletService,OfferRepository offerRepository,BetAgreementRepository betAgreementRepository){
        this.walletService=Objects.requireNonNull(walletService);
        this.offerRepository=Objects.requireNonNull(offerRepository);
        this.betAgreementRepository=Objects.requireNonNull(betAgreementRepository);
    }


    public void execute(long offerId,long takerUserId,Money stake){
        Offer offer=offerRepository.findById(offerId);
        offer.consume(stake);
        BetAgreement agreement=new BetAgreement(offer.id(), offer.makerUserId(), takerUserId, offer.outcomeId(), offer.position(), offer.odds(), stake);

        walletService.reserve(agreement.makerUserId(),agreement.makerRisk());
        walletService(agreement.takerUserId(),agreement.takerRisk());

        offerRepository.save(offer);
        betAgreementRepository.save(agreement);
    }


}
