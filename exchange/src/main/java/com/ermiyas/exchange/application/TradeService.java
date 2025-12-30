package com.ermiyas.exchange.application;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.OfferRepository;
import com.ermiyas.exchange.domain.repository.WalletRepository;
import com.ermiyas.exchange.domain.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final BetRepository betRepository;

    @Transactional
    public void matchBet(Long offerId, Long takerUserId, Money makerStakeToMatch) {
        // 1. Find Offer with PESSIMISTIC_WRITE lock
        Offer offer = offerRepository.findByIdWithLock(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        // 2. Find Taker
        Wallet takerWallet = walletRepository.findByUserId(takerUserId)
                .orElseThrow(() -> new RuntimeException("Taker wallet not found"));

        // 3. Domain Logic: Create the Bet contract
        String ref = "MATCH_" + UUID.randomUUID().toString().substring(0, 8);
        Bet bet = offer.fill(makerStakeToMatch, takerWallet.getUser(), ref);

        // 4. Financial Logic: Reserve Taker's Liability
        takerWallet.reserve(bet.getTakerLiability());

        // 5. Save all changes
        betRepository.save(bet);
        walletRepository.save(takerWallet);
        offerRepository.save(offer);
    }
}