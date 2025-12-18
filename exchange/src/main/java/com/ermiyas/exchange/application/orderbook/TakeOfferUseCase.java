package com.ermiyas.exchange.application.orderbook;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.Position; // Corrected domain import
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import com.ermiyas.exchange.domain.wallet.WalletService;

import java.util.Objects;

/**
 * Orchestrates the scenario where a user takes (matches) an existing offer.
 */
public class TakeOfferUseCase {
    private final WalletService walletService;
    private final OfferRepository offerRepository;
    private final BetAgreementRepository betAgreementRepository;

    public TakeOfferUseCase(WalletService walletService, OfferRepository offerRepository, BetAgreementRepository betAgreementRepository) {
        this.walletService = Objects.requireNonNull(walletService);
        this.offerRepository = Objects.requireNonNull(offerRepository);
        this.betAgreementRepository = Objects.requireNonNull(betAgreementRepository);
    }

    public void execute(long offerId, long takerUserId, Money stake) {
        // 1. Fetch the offer from the repository
        Offer offer = offerRepository.findById(offerId);
        
        // 2. Reduce the offer's remaining stake
        offer.consume(stake);
        
        // 3. Create the agreement (Match the 7-parameter constructor)
        BetAgreement agreement = new BetAgreement(
            offer.id(), 
            offer.makerUserId(), 
            takerUserId, 
            offer.outcomeId(), 
            offer.position(), 
            offer.odds(), 
            stake
        );

        // 4. RESERVE PHASE: Use the risk logic already inside BetAgreement
        // This ensures enough money is locked for the final settlement
        walletService.reserve(agreement.takerUserId(), agreement.takerRisk());

        // 5. Persist the changes
        offerRepository.save(offer);
        betAgreementRepository.save(agreement);
    }
}