package com.ermiyas.exchange.application;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
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
    public void matchBet(Long offerId, Long takerUserId, Money makerStakeToMatch) throws ExchangeException {
        
        Offer offer = offerRepository.findByIdWithLock(offerId);
        if (offer == null) {
            throw new ExchangeException("Offer not found with ID: " + offerId);
        }

        
        Wallet takerWallet = walletRepository.getByUserIdWithLock(takerUserId);
        if (takerWallet == null) {
            throw new ExchangeException("Taker wallet not found for User ID: " + takerUserId);
        }

        
        String ref = "MATCH_" + UUID.randomUUID().toString().substring(0, 8);
        Bet bet = offer.fill(makerStakeToMatch, takerWallet.getUser(), ref);

        
        takerWallet.reserve(bet.getTakerLiability());

        betRepository.save(bet);
        walletRepository.save(takerWallet);
        offerRepository.save(offer);
    }
}