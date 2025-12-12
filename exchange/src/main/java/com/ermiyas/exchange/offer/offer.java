package com.ermiyas.exchange.domain.offer;
import com.ermiyas.exchange.domain.Money;
import com.ermiyas.exchange.domain.Odds;

import java.time.Instant;

public class offer{
    private final long int id;
    private final long int makerUserId;
    private final long int outcomeId;
    private final Odds odds;
    private final Money inititalStake;
    private Money remainingStake;
    private final Instant createdAt;
    public offer(long id, long makerUserId,long outcomeId,Odds odds,Money stake)
{
    
}
    

}