package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.logic.SettlementStrategyFactory;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider.SportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REFACTORED: AdminSettlementService (OCP Friendly)
 * * This service is now closed for modification.
 * * It uses the Strategy Factory for rules and SportRequest for data.
 * * No lambda expressions or streams are used.
 */
@Service
@RequiredArgsConstructor
public class AdminSettlementService {

    private final EventRepository eventRepository;
    private final BetRepository betRepository;
    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final SportsDataProvider sportsDataProvider;
    private final SettlementStrategyFactory strategyFactory;
    private final CommissionPolicy defaultPolicy; // Injected to follow OCP

    @Transactional(rollbackFor = Exception.class)
    public void settleMarketResults(AdminUser admin, List<String> externalIds) throws ExchangeException {
        validateAdmin(admin);

        for (int i = 0; i < externalIds.size(); i++) {
            String extId = externalIds.get(i);
            try {
                processEventSettlement(extId);
            } catch (Exception e) {
                // Robustness: Continue with next event if one fails
            }
        }
    }

    private void processEventSettlement(String externalId) throws ExchangeException {
        // Safe Handling: Replaced lambda orElseThrow with traditional check
        Optional<Event> eventOpt = eventRepository.getByExternalId(externalId);
        if (!eventOpt.isPresent()) {
            throw new IllegalBetException("Target event not found: " + externalId);
        }
        Event event = eventOpt.get();

        if (event.getStatus() != EventStatus.OPEN) {
            return;
        }

        // 
        SportRequest request = new SportRequest(event.getLeague(), event.getMarketType());
        Map<String, Integer[]> leagueScores = sportsDataProvider.fetchScores(request);
        Integer[] scores = leagueScores.get(externalId);
        
        if (scores == null) {
            throw new IllegalBetException("External API Error: No score found for " + externalId);
        }
        
        // OCP: Ask the Factory for the strategy
        SettlementStrategy strategy = strategyFactory.getStrategy(event.getMarketType());
        
        // Delegate result processing to the domain entity
        event.processResult(scores[0], scores[1], strategy);

        // 3. Payout Logic: Uses injected policy instead of hardcoded 0.05
        resolveAllBets(event, defaultPolicy);

        // 4. Finalize
        cleanupUnmatchedOffers(event);
        event.markAsSettled();
        eventRepository.save(event);
    }

    private void resolveAllBets(Event event, CommissionPolicy policy) throws ExchangeException {
        List<Bet> bets = betRepository.findAllByOfferEventId(event.getId());
        for (int i = 0; i < bets.size(); i++) {
            Bet bet = bets.get(i);
            bet.resolve(event.getResult(), policy);
            betRepository.save(bet);
        }
    }

    private void cleanupUnmatchedOffers(Event event) throws ExchangeException {
        List<Offer> offers = event.getOffers();
        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                offer.cancel();
                
                if (offer.getMaker() instanceof StandardUser) {
                    StandardUser player = (StandardUser) offer.getMaker();
                    player.getWallet().unreserve(offer.getRemainingStake());
                    walletRepository.save(player.getWallet());
                } else {
                    throw new IllegalBetException("Integrity Error: Found an Admin with an active market offer.");
                }
                
                offerRepository.save(offer);
            }
        }
    }

    private void validateAdmin(AdminUser admin) {
        if (admin == null) {
            // Replaced IllegalAccessError with a runtime exception to avoid linkage issues
            throw new SecurityException("Security Violation: Unauthorized admin access detected.");
        }
    }
}