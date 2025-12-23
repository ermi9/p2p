package com.ermiyas.exchange.application.orderbook;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
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
    /**
     * 
     * @param offerId the challenge being accepted
     * @param takerUserId the person accepting the challenge
     * @param stake the portion of the challenger's stake to be matched
     */

    public void execute(long offerId, long takerUserId, Money amountToMatch) {
        // 1. Fetch the challenge
        Offer offer = offerRepository.findById(offerId);
        
        // 2. Ensure the challenger is not accepting their own offer
        if(offer.makerUserId()==takerUserId)
            throw new IllegalArgumentException("A challenger cannot accept their own challenge");

        // 3. COnsume the offer's remaining stake (Handles InsufficientFunds internally)
        offer.consume(amountToMatch);
        
        // 4. Create the agreement (Match the 7-parameter constructor)
        BetAgreement agreement = new BetAgreement(
            offer.id(), 
            offer.makerUserId(), 
            takerUserId, 
            offer.outcomeId(), 
            offer.odds(), 
            amountToMatch
        );

        // 5. RESERVE PHASE: Use the risk logic already inside BetAgreement
        // This ensures enough money is locked for the final settlement
        walletService.reserve(agreement.takerUserId(), agreement.takerRisk());

        // 6. Persist the changes
        offerRepository.save(offer);
        betAgreementRepository.save(agreement);
    }
}