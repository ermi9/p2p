package com.ermiyas.exchange.application;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.repository.EventRepository;
import com.ermiyas.exchange.domain.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final EventRepository eventRepository;
    private final BetRepository betRepository;
    private final CommissionPolicy commissionPolicy;

    @Transactional
    public void settleEvent(Long eventId, int homeScore, int awayScore) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Update scores and determine winner in the domain
        event.determineWinner(homeScore, awayScore);

        List<Bet> bets = betRepository.findAllByOfferEventId(eventId);

        for (Bet bet : bets) {
            Wallet makerWallet = bet.getMaker().getWallet();
            Wallet takerWallet = bet.getTaker().getWallet();
            String ref = "SETTLE_" + bet.getId();

            if (event.getResult() == bet.getOffer().getPredictedOutcome()) {
                // Maker wins (Backer)
                makerWallet.settleWin(bet.getMakerStake(), bet.getTakerLiability(), commissionPolicy);
                takerWallet.settleLoss(bet.getTakerLiability());
            } else {
                // Takers win (Layers)
                takerWallet.settleWin(bet.getTakerLiability(), bet.getMakerStake(), commissionPolicy);
                makerWallet.settleLoss(bet.getMakerStake());
            }
            bet.setStatus(BetStatus.SETTLED);
        }

        event.markAsSettled();
        eventRepository.save(event);
    }
}