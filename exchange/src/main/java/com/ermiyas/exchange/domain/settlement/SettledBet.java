package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.OfferId;
import com.ermiyas.exchange.domain.orderbook.BetFillAgreement;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable record representing one fill agreement that has now been settled.
 */
public final class SettledBet {

    private final OfferId offerId;
    private final long takerUserId;
    private final Money makerStakePortion;
    private final Money takerLiability;
    private final Odds odds;
    private final String winningOutcome;
    private final Instant settledAt;
    private final String reference;

    private SettledBet(
            OfferId offerId,
            long takerUserId,
            Money makerStakePortion,
            Money takerLiability,
            Odds odds,
            String winningOutcome,
            Instant settledAt,
            String reference
    ) {
        this.offerId = Objects.requireNonNull(offerId, "offerId");
        this.makerStakePortion = Objects.requireNonNull(makerStakePortion, "makerStakePortion");
        this.takerLiability = Objects.requireNonNull(takerLiability, "takerLiability");
        this.odds = Objects.requireNonNull(odds, "odds");
        this.winningOutcome = Objects.requireNonNull(winningOutcome, "winningOutcome");

        if (winningOutcome.isBlank()) {
            throw new IllegalArgumentException("winningOutcome cannot be blank");
        }

        this.settledAt = Objects.requireNonNull(settledAt, "settledAt");
        
        if (reference.isBlank()) {
            throw new IllegalArgumentException("reference cannot be blank");
        }
        
        this.takerUserId = takerUserId;
        this.reference = reference;
    }

    public static SettledBet fromFill(BetFillAgreement fill, String winningOutcome) {
        Objects.requireNonNull(fill);
        Objects.requireNonNull(winningOutcome);

        return new SettledBet(
                fill.offerId(),
                fill.takerUserId(),
                fill.makerStakePortion(),
                fill.liability(),
                fill.odds(),
                winningOutcome,
                Instant.now(),
                fill.reference()
        );
    }

    //getters
    public OfferId offerId() { return offerId; }
    public long takerUserId() { return takerUserId; }
    public Money makerStakePortion() { return makerStakePortion; }
    public Money takerLiability() { return takerLiability; }
    public Odds odds() { return odds; }
    public String winningOutcome() { return winningOutcome; }
    public Instant settledAt() { return settledAt; }
    public String reference() { return reference; }
}
