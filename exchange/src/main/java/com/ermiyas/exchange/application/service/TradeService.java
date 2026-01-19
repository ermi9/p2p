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
import java.util.Objects;

/**
 * Trade Orchestration Service.
 * Coordinates the matching process between a Taker and an existing Liquidity Offer.
 */
@Service
@RequiredArgsConstructor
public class TradeService {

    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final BetRepository betRepository;

    /**
     * Orchestrates a Peer-to-Peer trade match.
     * Uses pessimistic locking to prevent race conditions during heavy market activity.
     */
    @Transactional(rollbackFor = Exception.class)
    public void matchBet(Long offerId, Long takerUserId, Money makerStakeToMatch) throws ExchangeException {
        
        // 1. Fetch and Lock the Offer to ensure no other user matches it simultaneously
        Offer offer = offerRepository.findByIdWithLock(offerId)
                .orElseThrow(() -> new IllegalBetException("Trade Error: The offer (#" + offerId + ") is no longer available."));

        // 2. Security Check: Prevent Self-Matching (A user cannot match their own offer)
        if (Objects.equals(offer.getMaker().getId(), takerUserId)) {
            throw new IllegalBetException("Trade Violation: You cannot match your own offer. This is considered a wash trade.");
        }

        // 3. Market State Check: Ensure the event is still taking bets
        if (offer.getEvent().getStatus() != EventStatus.OPEN) {
            throw new IllegalBetException("Trade Failed: The market for this event is now " + offer.getEvent().getStatus() + ".");
        }

        // 4. Fetch and Lock Taker's Wallet
        Wallet takerWallet = walletRepository.getByUserIdWithLock(takerUserId)
                .orElseThrow(() -> new SecurityException("Trade Error: Wallet for user #" + takerUserId + " not found."));

        if (!(takerWallet.getUser() instanceof StandardUser taker)) {
            throw new IllegalBetException("Security Violation: Only player accounts can participate in trades.");
        }
        
        // 5. Orchestration: Calculate Liability and Create Bet
        // The offer.fill() method handles the math and throws descriptive errors if
        // the taker tries to match more than what is available.
        String ref = "MATCH_" + UUID.randomUUID().toString().substring(0, 8);
        Bet bet = offer.fill(makerStakeToMatch, taker, ref);

        // 6. Wallet Reservation: Hold the Taker's liability in escrow.
        // The wallet.reserve() method now throws an accurate 'Insufficient Funds' message
        // if the taker doesn't have enough to cover the liability.
        takerWallet.reserve(bet.getTakerLiability());

        // 7. Final Persistence
        betRepository.save(bet);
        walletRepository.save(takerWallet);
        offerRepository.save(offer);
    }
}