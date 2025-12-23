package com.ermiyas.exchange.referenceOdds.infrastructure.oddsapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.referenceOdds.domain.*;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public class OddsApiMapper {

    /**
     * Map a single event JSON to a Fixture.
     */
    public Fixture toFixture(JsonNode event) {

        String eventId = event.get("id").asText();
        String leagueKey = event.get("sport_key").asText();
        String homeTeam = event.get("home_team").asText();
        String awayTeam = event.get("away_team").asText();
        Instant kickoff = Instant.parse(event.get("commence_time").asText());

        return new Fixture(
                eventId,
                leagueKey,
                homeTeam,
                awayTeam,
                kickoff
        );
    }

    /**
     * Map odds JSON to a ReferenceOddsSnapshot (H2H only).
     */
    public ReferenceOddsSnapshot toH2HSnapshot(
            JsonNode oddsJson,
            Fixture fixture,
            String providerId
    ) {

        Map<Outcome, Odds> oddsMap = new EnumMap<>(Outcome.class);

        JsonNode bookmakers = oddsJson.get("bookmakers");
        if (bookmakers == null || !bookmakers.isArray() || bookmakers.isEmpty()) {
            return null;
        }

        // Take FIRST bookmaker only (simple & deterministic)
        JsonNode markets = bookmakers.get(0).get("markets");
        if (markets == null || !markets.isArray()) {
            return null;
        }

        for (JsonNode market : markets) {
            if (!"h2h".equals(market.get("key").asText())) {
                continue;
            }

            for (JsonNode outcome : market.get("outcomes")) {
                String name = outcome.get("name").asText();
                double price = outcome.get("price").asDouble();

                if (name.equals(fixture.homeTeam())) {
                    oddsMap.put(Outcome.HOME, new Odds(price));
                } else if (name.equals(fixture.awayTeam())) {
                    oddsMap.put(Outcome.AWAY, new Odds(price));
                } else {
                    oddsMap.put(Outcome.DRAW, new Odds(price));
                }
            }
        }

        if (oddsMap.isEmpty()) {
            return null;
        }

        return new ReferenceOddsSnapshot(
                fixture.externalEventId(),
                providerId,
                MarketType.H2H,
                oddsMap,
                Instant.now()
        );
    }
}
