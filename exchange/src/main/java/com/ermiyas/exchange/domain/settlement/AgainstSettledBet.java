package com.ermiyas.exchange.domain.settlement;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;

public final class AgainstSettledBet extends SettledBet{
    public AgainstSettledBet(long makerUserId,long takerUserId,Money amount,Odds odds){
        super(makerUserId,takerUserId,amount,odds);
    }
    @Override
    public boolean isWinning(ActualOutcome outcome){
        return outcome==ActualOutcome.OUTCOME_DID_NOT_HAPPEN;
    }
    
}
