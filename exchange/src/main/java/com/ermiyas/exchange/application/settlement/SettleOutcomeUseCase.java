package com.ermiyas.exchange.application.settlement;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.OfferId;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;
import com.ermiyas.exchange.domain.settlement.BetSide; // ← NEW import added
import com.ermiyas.exchange.domain.settlement.SettlementResult;
import com.ermiyas.exchange.domain.settlement.SettledBet;
import com.ermiyas.exchange.application.ports.OfferRepository;

import java.util.Objects;

public final class SettleOutcomeUseCase {

    private final OfferRepository offerRepository;

    public SettleOutcomeUseCase(OfferRepository offerRepository) {
        this.offerRepository = Objects.requireNonNull(offerRepository, "offerRepository");
    }

    public SettlementResult execute(OfferId offerId, BetSide winningSide) {
        Objects.requireNonNull(offerId, "offerId");
        Objects.requireNonNull(winningSide, "winningSide"); // optional but fine

        Offer offer = offerRepository.findById(offerId);
        if (offer == null) {
            throw new IllegalStateException("Offer not found");
        }

        // Create outcome fact (still string-based for market result)
        ActualOutcome outcome = ActualOutcome.now(winningSide.name());

        // Convert each fill into settled facts
        for (var fill : offer.fills()) {
            // CHANGED: removed loose string, using enum instead
            SettledBet settled = SettledBet.fromFill(fill, fill.reference());
            // infra will persist later
        }

        // Totals declared for correctness
        Money makerProfitTotal = Money.zero();
        Money makerLossTotal = Money.zero();
        Money takerProfitTotal = Money.zero();
        Money takerLossTotal = Money.zero();

        if (winningSide == BetSide.MAKER) {
            // Maker wins → taker pays liability
            for (var fill : offer.fills()) {
                makerProfitTotal = makerProfitTotal.plus(fill.liability());
                takerLossTotal = takerLossTotal.plus(fill.liability());
            }
            return SettlementResult.makerWins(makerProfitTotal, takerLossTotal, offerId.toString());
        } else {
            // Taker wins → maker loses stake portion, taker gains profit
            for (var fill : offer.fills()) {
                makerLossTotal = makerLossTotal.plus(fill.makerStakePortion());
                takerProfitTotal = takerProfitTotal.plus(
                        fill.makerStakePortion().multiply(fill.odds().profitMultiplier())
                );
            }
            return SettlementResult.takerWins(makerLossTotal, takerProfitTotal, offerId.toString());
        }
    }
}
