package com.ermiyas.exchange.application;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.repository.*;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminSyncService {

    private final SportsDataProvider sportsDataProvider;
    private final EventRepository eventRepository;
    private final BetRepository betRepository;
    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    
    // Using your updated CommissionPolicy default constructor (5%)
    private final CommissionPolicy commissionPolicy = new CommissionPolicy();

    @Transactional
    public void syncLeagues(List<String> leagues) {
        for (String league : leagues) {
            List<Event> fixtures = sportsDataProvider.fetchUpcomingFixtures(league);
            for (Event f : fixtures) {
                if (eventRepository.findByExternalId(f.getExternalId()).isEmpty()) {
                    eventRepository.save(f);
                }
            }
        }
    }

    @Transactional
    public void refreshOdds(List<String> leagues) {
        for (String league : leagues) {
            Map<String, List<Odds>> oddsMap = sportsDataProvider.fetchBestOdds(league);
            oddsMap.forEach((id, oddsList) -> {
                eventRepository.findByExternalId(id).ifPresent(event -> {
                    event.setRefHomeOdds(oddsList.get(0));
                    event.setRefAwayOdds(oddsList.get(1));
                    event.setRefDrawOdds(oddsList.get(2));
                    eventRepository.save(event);
                });
            });
        }
    }

    @Transactional
    public void refreshScores(List<String> leagues) {
        for (String league : leagues) {
            Map<String, Integer[]> scores = sportsDataProvider.fetchScores(league);
            scores.forEach((id, score) -> {
                eventRepository.findByExternalId(id).ifPresent(event -> {
                    // Only settle if match is OPEN and results are available from API
                    if (event.getStatus() == EventStatus.OPEN) {
                        event.determineWinner(score[0], score[1]);
                        
                        // 1. Payout all matched bets associated with this event
                        processPayouts(event);
                        
                        // 2. Return unmatched stakes (The cleanup for $80 remaining)
                        cleanupUnmatchedOffers(event);
                        
                        event.setStatus(EventStatus.SETTLED);
                        eventRepository.save(event);
                    }
                });
            });
        }
    }

    private void processPayouts(Event event) {
        // Uses the findAllByOffer_EventId method we added to BetRepository
        List<Bet> bets = betRepository.findAllByOfferEventId(event.getId());
        Outcome finalResult = event.getResult();

        for (Bet bet : bets) {
            Wallet makerWallet = walletRepository.findByUserId(bet.getOffer().getMaker().getId())
                    .orElseThrow(() -> new RuntimeException("Maker Wallet not found"));
            Wallet takerWallet = walletRepository.findByUserId(bet.getTaker().getId())
                    .orElseThrow(() -> new RuntimeException("Taker Wallet not found"));

            // Check if Maker's prediction matches the actual result
            if (bet.getOffer().getPredictedOutcome() == finalResult) {
                // MAKER WINS: Releases their matched stake + profit from Taker
                makerWallet.settleWin(bet.getMakerStake(), bet.getTakerLiability(), commissionPolicy);
                takerWallet.settleLoss(bet.getTakerLiability());
            } else {
                // TAKER WINS: Releases their liability + profit from Maker
                takerWallet.settleWin(bet.getTakerLiability(), bet.getMakerStake(), commissionPolicy);
                makerWallet.settleLoss(bet.getMakerStake());
            }
            
            bet.setStatus(BetStatus.SETTLED);
            betRepository.save(bet);
        }
    }

    private void cleanupUnmatchedOffers(Event event) {
        // Uses the findAllByEventId method we added to OfferRepository
        List<Offer> offers = offerRepository.findAllByEventId(event.getId());
        for (Offer offer : offers) {
            // Return any stake that was never matched by a Taker
            if (offer.getRemainingStake().value().signum() > 0) {
                Wallet makerWallet = walletRepository.findByUserId(offer.getMaker().getId())
                        .orElseThrow(() -> new RuntimeException("Maker Wallet not found"));
                
                // Return the 'Ghost' money to available balance
                makerWallet.unreserve(offer.getRemainingStake());
                walletRepository.save(makerWallet);
            }
            offer.setStatus(OfferStatus.CANCELLED);
            offerRepository.save(offer);
        }
    }
}