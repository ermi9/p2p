package com.ermiyas.exchange.referenceOdds.domain;

import com.ermiyas.exchange.common.Odds;

import java.time.Instant;
import java.util.Map;

public final class ReferenceOddsSnapshot {

    private final String externalEventId;
    private final String providerId;
    private final MarketType marketType;
    private final Map<Outcome, Odds> odds;
    private final Instant fetchedAt;

    public ReferenceOddsSnapshot(
            String externalEventId,
            String providerId,
            MarketType marketType,
            Map<Outcome, Odds> odds,
            Instant fetchedAt
    ) {
        this.externalEventId = externalEventId;
        this.providerId = providerId;
        this.marketType = marketType;
        this.odds = Map.copyOf(odds);
        this.fetchedAt = fetchedAt;
    }

    public String externalEventId() {
        return externalEventId;
    }

    public String providerId() {
        return providerId;
    }

    public MarketType marketType() {
        return marketType;
    }

    public Map<Outcome, Odds> odds() {
        return odds;
    }

    public Instant fetchedAt() {
        return fetchedAt;
    }
}
