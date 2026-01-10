package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * PURE OOP: Offer Service (Maker Logic).
 */
@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
       Creates a new offer from a "Maker".
     * Validates market state and reserves the maker's stake to ensure payout capability.
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOffer(Long eventId, Long makerUserId, Outcome outcome, Odds odds, Money stake) 
            throws ExchangeException {
        
        // 1. Validate Event status
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalBetException("Offer Error: Event not found."));
        
        if (event.getStatus() != EventStatus.OPEN) {
            throw new IllegalBetException("Offer Error: Market is " + event.getStatus() + " and no longer accepting offers.");
        }

        // 2. Identify and Validate Maker
        User user = userRepository.findById(makerUserId)
                .orElseThrow(() -> new IllegalBetException("Offer Error: User not found."));

        if (!(user instanceof StandardUser maker)) {
            throw new IllegalBetException("Security Violation: Only standard users can provide liquidity.");
        }

        // 3.  Reserve the stake in the maker's wallet
        // This moves money from 'available' to 'reserved'
        maker.getWallet().reserve(stake);

        // 4. Build the Offer entity using provided model logic
        Offer offer = Offer.builder()
                .maker(maker)
                .event(event)
                .predictedOutcome(outcome)
                .odds(odds)
                .originalStake(stake)
                .remainingStake(stake)
                .status(OfferStatus.OPEN)
                .build();

        // 5. Persist
        walletRepository.save(maker.getWallet());
        return offerRepository.save(offer).getId();
    }

    /**
     * Allows a Maker to withdraw their unmatched funds.
     * Returns the remaining stake from the 'reserved' pool back to 'available'.
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOffer(Long offerId, Long userId) throws ExchangeException {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalBetException("Cancel Error: Offer not found."));

        // Security check: Only the owner can cancel
        if (!Objects.equals(offer.getMaker().getId(), userId)) {
            throw new IllegalBetException("Security Violation: Unauthorized attempt to cancel someone else's offer.");
        }

        // Use the Domain Logic inside the Offer entity for state transition
        Money stakeToReturn = offer.getRemainingStake();
        offer.cancel();

        // Unreserve the funds in the wallet
        if (offer.getMaker() instanceof StandardUser maker) {
            maker.getWallet().unreserve(stakeToReturn);
            walletRepository.save(maker.getWallet());
        }

        offerRepository.save(offer);
    }
}