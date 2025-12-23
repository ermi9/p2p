package com.ermiyas.exchange.domain.settlement;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import java.math.BigDecimal;
// this is the abstract base
public abstract class SettledBet {
protected final long makerUserId;
protected final long takerUserId;
protected final Money amount;
protected final Odds odds;

protected SettledBet(long makerUserId,long takerUserId,Money amount,Odds odds){
    this.makerUserId=makerUserId;
    this.takerUserId=takerUserId;
    this.amount=amount;
    this.odds=odds;
}

public abstract boolean isWinning( ActualOutcome outcome);

/*payout calculation logic here */
public Money payout(){
    BigDecimal profit= amount.value().multiply(odds.profitMultiplier());
    return new Money(amount.value().add(profit));//type cast from MOney to BigDecmial
}

public long makerUserId(){
    return makerUserId;
}

public long takerUserId(){
    return takerUserId;
}

}