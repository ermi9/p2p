package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.common.Money;
import java.util.Objects;

public final class SettlementResult {

    private final BetSide winningSide;
    private final Money makerProfit;
    private final Money takerProfit;
    private final Money platformCommission;
    private final String reference;

    private SettlementResult(
            BetSide winningSide,
            Money makerProfit,
            Money takerProfit,
            Money platformCommission,
            String reference
    ) {
        this.winningSide = Objects.requireNonNull(winningSide, "winningSide");
        this.makerProfit = Objects.requireNonNull(makerProfit, "makerProfit");
        this.takerProfit = Objects.requireNonNull(takerProfit, "takerProfit");
        this.platformCommission = Objects.requireNonNull(platformCommission, "platformCommission");
        this.reference = Objects.requireNonNull(reference, "reference");

        if (reference.isBlank())
            throw new IllegalArgumentException("reference cannot be blank");

        // invariant: only one side can profit
        if (!makerProfit.equals(Money.zero()) && !takerProfit.equals(Money.zero()))
            throw new IllegalStateException("Only one side can profit");
    }

    public static SettlementResult makerWins(
            Money makerProfit,
            Money platformCommission,
            String reference
    ) {
        return new SettlementResult(
                BetSide.MAKER,
                makerProfit,
                Money.zero(),
                platformCommission,
                reference
        );
    }

    public static SettlementResult takerWins(
            Money takerProfit,
            Money platformCommission,
            String reference
    ) {
        return new SettlementResult(
                BetSide.TAKER,
                Money.zero(),
                takerProfit,
                platformCommission,
                reference
        );
    }

    public BetSide winningSide() {
        return winningSide;
    }

    public Money makerProfit() {
        return makerProfit;
    }

    public Money takerProfit() {
        return takerProfit;
    }

    public Money platformCommission() {
        return platformCommission;
    }

    public String reference() {
        return reference;
    }
}
