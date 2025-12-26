package com.ermiyas.exchange.referenceOdds.infrastructure;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.referenceOdds.domain.*;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ReferenceOddsService {

    private final OddsApiClient client;

    public ReferenceOddsService(OddsApiClient client) {
        this.client = client;
    }

    public List<Fixture> fetchUpcomingFixtures(League league, int maxEvents) throws Exception {
        JsonNode events = client.getEvents(league.oddsApiKey());
        List<Fixture> fixtures = new ArrayList<>();
        Instant now = Instant.now();

        int count = 0;
        for (JsonNode event : events) {
            if (count >= maxEvents) break;

            String eventId = event.get("id").asText();
            String home = event.get("home_team").asText();
            String away = event.get("away_team").asText();
            Instant kickoff = Instant.parse(event.get("commence_time").asText());

            if (kickoff.isBefore(now)) continue;

            fixtures.add(new Fixture(eventId, league, home, away, kickoff));
            count++;
        }

        return fixtures;
    }

    public List<ReferenceOddsSnapshot> fetchOddsForFixture(Fixture fixture) throws Exception {
        JsonNode oddsJson = client.getEventOdds(fixture.league().oddsApiKey(), fixture.id());
        List<ReferenceOddsSnapshot> snapshots = new ArrayList<>();

        if (oddsJson.get("bookmakers") == null || !oddsJson.get("bookmakers").isArray()) return snapshots;

        for (JsonNode bookmaker : oddsJson.get("bookmakers")) {
            JsonNode markets = bookmaker.get("markets");
            if (markets == null || !markets.isArray()) continue;

            for (JsonNode market : markets) {
                if (!"h2h".equals(market.get("key").asText())) continue;

                Map<Outcome, Money> prices = new EnumMap<>(Outcome.class);
                for (JsonNode outcome : market.get("outcomes")) {
                    Outcome o = Outcome.valueOf(outcome.get("name").asText().toUpperCase());
                    Money price = new Money(outcome.get("price").decimalValue());
                    prices.put(o, price);
                }

                snapshots.add(ReferenceOddsSnapshot.now(fixture, MarketType.of("H2H"), prices));
            }
        }

        return snapshots;
    }
}
