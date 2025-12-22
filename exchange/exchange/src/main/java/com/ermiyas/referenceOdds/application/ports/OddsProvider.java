package com.ermiyas.exchange.referenceOdds.application.ports;
import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.MarketType;
import com.ermiyas.exchange.referenceOdds.domain.ReferenceOddsSnapshot;

import java.util.time.Instant;
import java.util.List;
import java.util.Set;
/*
*Implementations are open for extension via new MarketTypes,
*without modifying the usecases.
*/
public interface OddsProvider {
/**
 * @return identifier of the provider(OddsAPI,Bet365, etc);
 * 
 */
String providerId();
/** 
 * @return MarketTypes supported by this provider
 * **/
Set<MarketType> supportedMarkets();
List<Fixture> fetchUpcomingFixtures(Instant from,Instant to);
List<ReferenceOddsSnapshot> fetchOdds(Fixture fixture,MarketType marketType);

    
}
