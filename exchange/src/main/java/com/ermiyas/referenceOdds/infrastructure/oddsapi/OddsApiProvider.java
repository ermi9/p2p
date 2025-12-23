package com.ermiyas.exchange.referenceOdds.infrastructure.oddsapi;
import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.MarketType;
import com.ermiyas.exchange.referenceOdds.domain.ReferenceOddsSnapshot;

import java.lang.ref.Reference;
import java.time.Instant;
import java.util.List;
import java.util.Set;



public class OddsApiProvider implements OddsProvider {
    private final OddsApiClient client;
    private final OddsApiMapper mapper;
    private static final Set<String> SUPPORTED_LEAGUES=Set.of(
        "soccer_epl",
        "soccer_serie_a",
        "soccer_spain_la_liga",
        "soccer_germany_bundesliga",
        "soccer_france_ligue_one"

    );
    public OddsApiProvider(OddsApiClient client,OddsApiMapper mapper){
        this.client=client;
        this.mapper=mapper;
    }
    @Override
    public String providerId(){
        return "ODDS_API";
    }
    @Override
    public Set<MarketType> supportedMarketTypes(){
        return Set.of(MarketType.H2H);
    }
    @Override
    public List<Fixture> fetchUpcomingFixtures(Instant from,Instant until){
        return SUPPORTED_LEAGUES.stream()
        .flatMap(league -> 
            client.fetchEvents(league).stream()
            .map(eventJson->
                mapper.toFixture(eventJson,league))
                .filter(f->!f.kickoffTime().isBefore(from))
                .filter(f -> f.kickoffTime().isBefore(until))
    ).toList();
    }
    @Override
    public List<ReferenceOddsSnapshot> fetchOdds(
        Fixture fixture,
        MarketType marketType
    ){
        if(marketType!=MarketType.H2H){
            return List.of();
        }
        return client.fetchEventOdds(
            fixture.leagueKey(),
            fixture.externalEventId()
        ).stream()
        .flatMap(json ->
                    mapper.toH2HSnapshots(json).stream()).toList();

        
    }

    
}
