package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.common.Money;
import java.util.Objects;

public final class SettlementResult {
    private final boolean makerWon;
    private final Money makerOutcomeEffect;
    private final Money takerOutcomeEffect;
    private final String reference;

    public SettlementResult(boolean makerWon, Money makerOutcomeEffect, Money takerOutcomeEffect,String reference) {
        this.makerWon = makerWon;
        this.makerOutcomeEffect = Objects.requireNonNull(makerOutcomeEffect,"makerOutcomeEffect");
        this.takerOutcomeEffect = Objects.requireNonNull(takerOutcomeEffect,"takerOutcomeEffect");
        this.reference=Objects.requireNonNull(reference,"reference");
        if(reference.isBlank())
            throw new IllegalArgumentException("reference cannot be blank");
    }
    public static SettlementResult makerWins(Money makerProfit,Money takerLoss,String reference){
        return new SettlementResult(true, makerProfit, takerLoss, reference);
    }
    public static SettlementResult takerWins(Money makerLoss,Money takerProfit, String reference){
        return new SettlementResult(false, makerLoss, takerProfit, reference);
    }
    public boolean makerWon(){
        return makerWon;
    }
    public Money makerOutcomeEffect(){
        return makerOutcomeEffect;
    }
    public Money takerOutcomeEffect(){
        return takerOutcomeEffect;
    }
    public String reference(){
        return reference;
    }

}
