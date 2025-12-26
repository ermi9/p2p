package com.ermiyas.exchange.referenceOdds.domain;

import com.ermiyas.exchange.common.Money;
import java.time.Instant;
import java.util.Objects;
import java.util.Map;

public final class ReferenceOddsSnapshot {

    private final Fixture fixture;
    private final MarketType market;
    private final Map<Outcome, Money> prices;
    private final Instant fetchedAt;

    public ReferenceOddsSnapshot(
            Fixture fixture,
            MarketType market,
            Map<Outcome, Money> prices,
            Instant fetchedAt
    ) {
        this.fixture = Objects.requireNonNull(fixture);
        this.market = Objects.requireNonNull(market);
        this.prices = Map.copyOf(Objects.requireNonNull(prices));
        this.fetchedAt = Objects.requireNonNull(fetchedAt);
    }

    public Fixture fixture() { return fixture; }
    public MarketType market() { return market; }
    public Map<Outcome, Money> prices() { return prices; }
    public Instant fetchedAt() { return fetchedAt; }

    public static ReferenceOddsSnapshot now(
            Fixture fixture,
            MarketType market,
            Map<Outcome, Money> prices
    ) {
        return new ReferenceOddsSnapshot(fixture, market, prices, Instant.now());
    }
}
