package com.ermiyas.exchange.domain.offer;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.orderbook.BetFillAgreement;
import com.ermiyas.exchange.domain.orderbook.OrderBookException;
import com.ermiyas.exchange.common.NegativeMoneyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Offer {

    private final OfferId id;
    private final Money originalStake;
    private final Odds odds;

    private Money remainingStake;
    private OfferStatus status;
    private final List<BetFillAgreement> fills = new ArrayList<>();

    private Offer(Money stake, Odds odds) {
        this.id = OfferId.newId();
        this.originalStake = stake;
        this.remainingStake = stake;
        this.odds = odds;
        this.status = OfferStatus.OPEN;
    }

    public static Offer create(Money stake, Odds odds) {
        Objects.requireNonNull(stake, "stake");
        Objects.requireNonNull(odds, "odds");

        if (stake.value().signum() < 0) {
            throw new NegativeMoneyException("Stake cannot be negative");
        }

        return new Offer(stake, odds);
    }

    public OfferId id() { return id; }
    public Money originalStake() { return originalStake; }
    public Money remainingStake() { return remainingStake; }
    public OfferStatus status() { return status; }
    public List<BetFillAgreement> fills() { return Collections.unmodifiableList(fills); }
    public Odds odds() { return odds; }

    /**
     * Many takers may attempt to fill until exhausted.
     */
    public BetFillAgreement fill(Money liability, long takerUserId, String reference) {
        Objects.requireNonNull(liability, "liability");
        Objects.requireNonNull(reference, "reference");

        if (liability.value().signum() <= 0) {
            throw new NegativeMoneyException("Liability must be positive");
        }

        // NEW: minimal domain exposure check added
        if (liability.compareTo(remainingStake) > 0) {
            throw new OrderBookException("Liability exceeds remaining stake exposure");
        }

        BigDecimal proportion = liability.value().divide(originalStake.value(), 4, RoundingMode.HALF_UP);
        Money stakeToConsume = originalStake.multiply(proportion);

        // Deduct once from remaining stake
        remainingStake = remainingStake.minus(stakeToConsume);

        BetFillAgreement agreement = BetFillAgreement.of(id, takerUserId, stakeToConsume, liability, odds, reference);
        fills.add(agreement);

        if (remainingStake.value().signum() == 0) {
            status = OfferStatus.TAKEN;
        } else {
            status = OfferStatus.PARTIALLY_TAKEN;
        }

        return agreement;
    }

    // CHANGED: exception type fixed to state violation
    public void cancel() {
        if (status != OfferStatus.OPEN) {
            throw new IllegalStateException("Only OPEN offers can be cancelled");
        }
        status = OfferStatus.CANCELLED;
    }

    // CHANGED: removed string outcome parameter, offer doesn’t decide winners
    public void markSettled() {
        if (fills.isEmpty()) {
            throw new IllegalStateException("Cannot mark SETTLED without fills");
        }
        status = OfferStatus.SETTLED;
    }
}
