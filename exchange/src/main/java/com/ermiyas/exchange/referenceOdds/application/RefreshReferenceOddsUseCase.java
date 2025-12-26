package com.ermiyas.exchange.referenceOdds.application;

import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.MarketType;
import com.ermiyas.exchange.referenceOdds.domain.Outcome;
import com.ermiyas.exchange.referenceOdds.domain.ReferenceOddsSnapshot;
import com.ermiyas.exchange.referenceOdds.application.ports.OddsProvider;

import java.util.Map;
import java.util.Objects;

/**
 * Use case: fetch the latest odds for a fixture, market, and outcome.
 */
public final class RefreshReferenceOddsUseCase {

    private final OddsProvider oddsProvider;

    public RefreshReferenceOddsUseCase(OddsProvider oddsProvider) {
        this.oddsProvider = Objects.requireNonNull(oddsProvider, "oddsProvider");
    }

    /**
     * Fetch the latest snapshot for a fixture and market.
     * @param fixture the match/fixture
     * @param market the market type (e.g., H2H)
     * @return snapshot containing all outcome prices
     */
    public ReferenceOddsSnapshot execute(Fixture fixture, MarketType market) {
        Objects.requireNonNull(fixture, "fixture");
        Objects.requireNonNull(market, "market");

        // Fetch all odds for this fixture & market
        ReferenceOddsSnapshot snapshot = oddsProvider.fetchOdds(fixture, market);

        // Return a new snapshot with updated fetch timestamp
        return ReferenceOddsSnapshot.now(
                snapshot.fixture(),
                snapshot.market(),
                snapshot.prices() // map of all outcomes and their prices
        );
    }

    /**
     * Convenience method to get the price for a specific outcome.
     */
    public double priceForOutcome(Fixture fixture, MarketType market, Outcome outcome) {
        Objects.requireNonNull(outcome, "outcome");

        ReferenceOddsSnapshot snapshot = execute(fixture, market);
        if (!snapshot.prices().containsKey(outcome)) {
            throw new IllegalArgumentException("No price found for outcome: " + outcome);
        }

        return snapshot.prices().get(outcome).value().doubleValue();
    }

    /**
     * Convenience method to get all outcome prices for a fixture & market.
     */
    public Map<Outcome, ?> allPrices(Fixture fixture, MarketType market) {
        ReferenceOddsSnapshot snapshot = execute(fixture, market);
        return snapshot.prices();
    }
}
