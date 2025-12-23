package com.ermiyas.exchange.referenceOdds.application;
import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.MarketType;
import com.ermiyas.exchange.referenceOdds.domain.ReferenceOddsSnapshot;

import java.util.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
/**
 * Use case responsible for refreshing reference fixtures and
 * reference odds from external providers
 * 
*/

public class RefreshReferenceOddsUseCase {
    private final List<OddsProvider> providers;
    private static final int LOOKAHEAD_DAYS=7;
    private static final Set<MarketType> ENABLED_MARKETS=Set.of(MarketType.H2H);
    public RefreshReferenceOddsUseCase(List<OddsProvider> providers){
        this.providers=List.copyOf(providers);
    }
    public void refresh(){
        java.time.Instant now=Instant.now();
        Instant until=now.plus(LOOKAHEAD_DAYS,ChronoUnit.DAYS);
        for (OddsProvider provider: providers){
            List<Fixture> fixtures=provider.fetchUpcomingFixtures(now,until);

            for(Fixture fixture: fixtures){
                for(MarketType market: ENABLED_MARKETS){
                    if(!provider.supportedMarkets().contains(market))
                        continue;
                    List<ReferenceOddsSnapshot> snapshots=provider.fetchOdds(fixture,market);
                    for(ReferenceOddsSnapshot snapshot:snapshots){
                        handleSnapshot(snapshot);
                    }
                }
            }
        }
    }
//will be extended later on if necessary
    private void handleSnapshot(ReferenceOddsSnapshot snapshot){

    }
}
