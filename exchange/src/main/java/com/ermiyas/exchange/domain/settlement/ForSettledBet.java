package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.common.Money;

public final class ForSettledBet extends SettledBet{

    public ForSettledBet(long makerUserId,long takerUserId,Money amount,Odds odds){
super(makerUserId,takerUserId,amount,odds);
    }
    @Override
    //trivial, OUTCOME_HAPPENED means the  who said 'FOR' is winning
    public boolean isWinning(ActualOuctcome outcome){
        return outcome==ActualOuctcome.OUTCOME_HAPPENED;
    }
    
}
