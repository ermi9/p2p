package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;

public final class ForSettledBet extends SettledBet{

    public ForSettledBet(long makerUserId,long takerUserId,Money amount,Odds odds){
super(makerUserId,takerUserId,amount,odds);
    }
    @Override
    //trivial, OUTCOME_HAPPENED means the  who said 'FOR' is winning
    public boolean isWinning(ActualOutcome outcome){
        return outcome==ActualOutcome.OUTCOME_HAPPENED;
    }
    
}
