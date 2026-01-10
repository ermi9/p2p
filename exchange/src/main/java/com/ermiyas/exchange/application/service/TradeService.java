package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

/**
 * PURE OOP: Trade Orchestration Service.
 * This service handles the "Matching" logic where a Taker decides to fill a Maker's Offer.
 * It coordinates the exchange between the Offer, the Taker's Wallet, and the final Bet.
 */
@Service
@RequiredArgsConstructor
public class TradeService {

    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final BetRepository betRepository;

    /**
     * Logic: Matches a Taker to an existing Offer.
     * Transactional: We use 'findByIdWithLock' to prevent two people from taking 
     * the same offer at the exact same millisecond (Race Conditions).
     * * @param offerId The ID of the maker's offer.
     * @param takerUserId The ID of the user trying to take the bet.
     * @param makerStakeToMatch How much of the maker's stake the taker wants to cover.
     */
    @Transactional(rollbackFor = Exception.class)
    public void matchBet(Long offerId, Long takerUserId, Money makerStakeToMatch) throws ExchangeException {
        
        // 1. Fetch the Offer. We use Optional here to avoid NullPointerExceptions.
        Optional<Offer> offerOpt = offerRepository.findByIdWithLock(offerId);
        if (!offerOpt.isPresent()) {
            throw new IllegalBetException("Trade Error: The offer you are looking for (#" + offerId + ") does not exist.");
        }
        Offer offer = offerOpt.get();

        // 2. Fetch the Taker's Wallet.
        Optional<Wallet> walletOpt = walletRepository.getByUserIdWithLock(takerUserId);
        if (!walletOpt.isPresent()) {
            throw new SecurityException("Trade Error: We couldn't find a wallet for User ID: " + takerUserId);
        }
        Wallet takerWallet = walletOpt.get();

        // 3. Safe Type Handling: Admins don't have wallets in our system.
        // We check if the wallet owner is a StandardUser (a player) before moving forward.
        if (!(takerWallet.getUser() instanceof StandardUser)) {
            throw new IllegalBetException("Security Violation: Admins are not allowed to participate in market trades.");
        }
        
        // Since we confirmed it's a StandardUser, we cast it for the domain call.
        StandardUser taker = (StandardUser) takerWallet.getUser();

        // 4. Orchestration: We tell the domain objects to do the work.
        // We generate a unique reference for the matching bet.
        String ref = "MATCH_" + UUID.randomUUID().toString().substring(0, 8);
        
        // The Offer 'fill' method handles the math of reducing the remaining stake 
        // and creating the Bet entity.
        Bet bet = offer.fill(makerStakeToMatch, taker, ref);

        // The Wallet 'reserve' method handles moving the taker's liability into 
        // a "held" state so they can't spend it elsewhere while the bet is live.
        takerWallet.reserve(bet.getTakerLiability());

        // 5. Persistence: Save everything back to the database.
        betRepository.save(bet);
        walletRepository.save(takerWallet);
        offerRepository.save(offer);
    }
}