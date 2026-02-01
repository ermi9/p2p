package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.User; // Added to handle base User type
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSettlementService {

    private final EventRepository eventRepository;
    private final BetRepository betRepository;
    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final CommissionPolicy defaultPolicy;

    public BigDecimal calculateTotalLockedStake() {
        List<Wallet> wallets = walletRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < wallets.size(); i++) {
            total = total.add(wallets.get(i).getReservedBalance().value());
        }
        return total;
    }

    public void settleMarketResults(AdminUser admin, List<String> externalIds) throws ExchangeException {
        validateAdmin(admin);
        for (int i = 0; i < externalIds.size(); i++) {
            String extId = externalIds.get(i);
            try {
                processEventSettlement(extId);
            } catch (Exception e) {
                // Catch block preserves the batch process if one fixture fails
                System.err.println("Settlement failed for fixture " + extId + ": " + e.getMessage());
            }
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    private void processEventSettlement(String externalId) throws ExchangeException {
        Optional<Event> eventOpt = eventRepository.getByExternalId(externalId);
        if (!eventOpt.isPresent()) {
            throw new IllegalBetException("Target event not found: " + externalId);
        }
        Event event = eventOpt.get();

        // Admin can only settle matches flagged as COMPLETED by the background sync
        if (event.getStatus() != EventStatus.COMPLETED) {
            throw new IllegalBetException("State Violation: Event must be COMPLETED before settlement.");
        }

        resolveAllBets(event, defaultPolicy);
        cleanupUnmatchedOffers(event);
        event.markAsSettled();
        eventRepository.save(event);
    }

    /**
        Uses dynamic dispatch to persist wallets through User.getWallet().
     */
    private void resolveAllBets(Event event, CommissionPolicy policy) throws ExchangeException {
        List<Bet> bets = betRepository.findAllByOfferEventId(event.getId());
        for (int i = 0; i < bets.size(); i++) {
            Bet bet = bets.get(i);
            
            // 1. Resolve bet
            bet.resolve(event.getResult(), policy);
            
            // 2. Persist Wallets
            User maker = bet.getOffer().getMaker();
            User taker = bet.getTaker();

            Wallet makerWallet=maker.getWallet();
            Wallet takerWallet=taker.getWallet();

            walletRepository.save(takerWallet);
            walletRepository.save(makerWallet);
            
            // 3. Persist the Bet: Updates status so it disappears from 'Active Bets'
            betRepository.save(bet);
        }
    }

    private void cleanupUnmatchedOffers(Event event) throws ExchangeException {
        List<Offer> offers = event.getOffers();
        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                offer.cancel();

                User maker=offer.getMaker();
                Wallet makerWallet=maker.getWallet();
                makerWallet.unreserve(offer.getRemainingStake());
                walletRepository.save(makerWallet);

                offerRepository.save(offer);
            }
        }
    }

    private void validateAdmin(AdminUser admin) {
        if (admin == null) {
            throw new SecurityException("Unauthorized admin access.");
        }
    }
}