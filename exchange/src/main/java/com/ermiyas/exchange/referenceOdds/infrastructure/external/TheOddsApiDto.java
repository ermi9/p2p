package com.ermiyas.exchange.referenceOdds.infrastructure.external;

import com.ermiyas.exchange.referenceOdds.domain.OddsSnapshot;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record TheOddsApiDto(
    String id,
    @JsonProperty("home_team") String homeTeam,
    @JsonProperty("away_team") String awayTeam,
    @JsonProperty("commence_time") String commenceTime,
    List<BookmakerDto> bookmakers
) {
    public List<OddsSnapshot> toSnapshots(Long internalEventId) {
        List<OddsSnapshot> snapshots = new ArrayList<>();

        if (bookmakers == null) return snapshots;

        for (BookmakerDto bookmaker : bookmakers) {
            for (MarketDto market : bookmaker.markets()) {
                // We only care about the head-to-head (win/draw/win) market
                if (!"h2h".equals(market.key())) continue;

                for (OutcomeDto outcome : market.outcomes()) {
                    String internalOutcomeName;

                    // Mapping logic: API Team Name -> Internal Database Label
                    if (outcome.name().equalsIgnoreCase(this.homeTeam)) {
                        internalOutcomeName = "Home";
                    } else if (outcome.name().equalsIgnoreCase(this.awayTeam)) {
                        internalOutcomeName = "Away";
                    } else if (outcome.name().equalsIgnoreCase("Draw")) {
                        internalOutcomeName = "Draw";
                    } else {
                        internalOutcomeName = outcome.name();
                    }

                snapshots.add(OddsSnapshot.builder()
                    .eventId(internalEventId)
                    .providerName(bookmaker.title())
                    .outcomeName(internalOutcomeName)
                    .price(java.math.BigDecimal.valueOf(outcome.price())) // Convert Double to BigDecimal
                    .fetchedAt(LocalDateTime.now())
                    .build());
                }
            }
        }
        return snapshots;
    }

    public record BookmakerDto(String key, String title, List<MarketDto> markets) {}
    public record MarketDto(String key, List<OutcomeDto> outcomes) {}
    public record OutcomeDto(String name, Double price) {}
}