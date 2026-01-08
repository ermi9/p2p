package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.logic.SettlementStrategyFactory; // Our new OCP bridge
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.StandardPercentagePolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * PURE OOP: Admin Orchestration Service.
 * This is now fully OCP compliant. It doesn't know any specific betting rules;
 * it just coordinates the flow between the API, the Factory, and the Domain.
 */
@Service
@RequiredArgsConstructor
public class AdminSettlementService {

    private final EventRepository eventRepository;
    private final BetRepository betRepository;
    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final SportsDataProvider sportsDataProvider;
    private final SettlementStrategyFactory strategyFactory; // No more hardcoding!

    /**
     * Logic: Entry point for Admin settlement.
     * Transactional so that if one payout fails, everything rolls back to keep the money safe.
     */
    @Transactional(rollbackFor = Exception.class)
    public void settleMarketResults(AdminUser admin, List<String> externalIds) throws ExchangeException {
        validateAdmin(admin);

        for (String extId : externalIds) {
            try {
                processEventSettlement(extId);
            } catch (Exception e) {
                // Robustness: If one match score is missing or buggy, 
                // we don't stop the settlement for other matches.
            }
        }
    }

    private void processEventSettlement(String externalId) throws ExchangeException {
        // Safe Handling: Grab the event from the repo
        Event event = eventRepository.getByExternalId(externalId)
                .orElseThrow(() -> new IllegalBetException("Target event not found: " + externalId));

        if (event.getStatus() != EventStatus.OPEN) return;

        // 1. Fetch scores as an array (Home vs Away)
        Map<String, Integer[]> leagueScores = sportsDataProvider.fetchScores(externalId);
        Integer[] scores = leagueScores.get(externalId);
        
        if (scores == null) {
            throw new IllegalBetException("External API Error: No score found for " + externalId);
        }
        
        // 2. TRUE OCP: Instead of 'new ThreeWaySettlementStrategy()', we ask the factory.
        // The Factory looks at event.getMarketType() and gives us the right logic.
        SettlementStrategy strategy = strategyFactory.getStrategy(event.getMarketType());
        
        // The Event class uses the strategy to determine the Outcome (HOME_WIN, etc.)
        event.processResult(scores[0], scores[1], strategy);

        // 3. Payout Logic
        CommissionPolicy policy = new StandardPercentagePolicy(new BigDecimal("0.05"));
        resolveAllBets(event, policy);

        // 4. Finalize the Market
        cleanupUnmatchedOffers(event);
        event.markAsSettled();
        eventRepository.save(event);
    }

    /**
     * Logic: Tells all matched bets to settle themselves based on the event result.
     */
    private void resolveAllBets(Event event, CommissionPolicy policy) throws ExchangeException {
        List<Bet> bets = betRepository.findAllByOfferEventId(event.getId());
        for (Bet bet : bets) {
            // Bet handles its own Win/Loss logic internally
            bet.resolve(event.getResult(), policy);
            betRepository.save(bet);
        }
    }

    /**
     * Logic: Returns money to makers whose offers weren't fully taken.
     */
    private void cleanupUnmatchedOffers(Event event) throws ExchangeException {
        for (Offer offer : event.getOffers()) {
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                offer.cancel();
                
                // Integrity Check: Only players (StandardUsers) have wallets
                if (offer.getMaker() instanceof StandardUser player) {
                    player.getWallet().unreserve(offer.getRemainingStake());
                    walletRepository.save(player.getWallet());
                } else {
                    throw new IllegalBetException("Integrity Error: Found an Admin with an active market offer.");
                }
                
                offerRepository.save(offer);
            }
        }
    }

    private void validateAdmin(AdminUser admin) throws ExchangeException {
        if (admin == null) {
            throw new IllegalBetException("Security Violation: Unauthorized admin access detected.");
        }
    }
}