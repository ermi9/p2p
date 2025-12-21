//Maker as a challenger model
package com.ermiyas.exchange.domain.orderbook;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.settlement.ActualOutcome; //

import java.util.Objects;

public final class BetAgreement {
    private final long offerId;
    private final long makerUserId;
    private final long takerUserId;
    private final long outcomeId;

    private final Odds odds;
    private final Money stakeMatched;
    
    private final Money makerRisk;//always equal to stakeMatched
    private final Money takerRisk;
    
    private boolean settled = false; //

    public BetAgreement(long offerId, long makerUserId, long takerUserId, long outcomeId, Odds odds, Money stakeMatched) {
        this.offerId = offerId;
        this.makerUserId = makerUserId;
        this.takerUserId = takerUserId;
        this.outcomeId = outcomeId;
        this.odds = Objects.requireNonNull(odds); //
        this.stakeMatched = Objects.requireNonNull(stakeMatched); //

        //the user who posted it always risks the stake he posted
        this.makerRisk=stakeMatched;
        this.takerRisk=odds.calculateLiability(stakeMatched);
    }

    public void markSettled() { //
        if(this.settled)
            throw new BetAlreadySettledException(offerId, makerUserId, takerUserId);
        this.settled=true;
    }

    public long winnerUserId(ActualOutcome outcome) { //
        boolean outcomeHappened = (outcome == ActualOutcome.OUTCOME_HAPPENED); //
        
        return outcomeHappened ? makerUserId : takerUserId; //
    }

    public long loserUserId(ActualOutcome outcome) { //
        return winnerUserId(outcome) == makerUserId ? takerUserId : makerUserId; //
    }


    // Getters
    public boolean isSettled(){
        return settled;
    }
    public long offerId() { return offerId; }
    public long makerUserId() { return makerUserId; }
    public long takerUserId() { return takerUserId; }
    public long outcomeId() { return outcomeId; }
    public Odds odds() { return odds; }
    public Money makerRisk() { return makerRisk; }
    public Money takerRisk() { return takerRisk; }
    public Money totalPayout() { return makerRisk.plus(takerRisk); }
}