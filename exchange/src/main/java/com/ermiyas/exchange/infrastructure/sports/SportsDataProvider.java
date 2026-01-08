package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League; // Assuming this location
import com.ermiyas.exchange.domain.model.MarketType; // Assuming this location
import com.ermiyas.exchange.domain.vo.Odds;
import java.util.List;
import java.util.Map;

/**
 * REFACTORED: SportsDataProvider (OCP Friendly)
 */
public interface SportsDataProvider {

    /**
     * Requirement: Check if the provider (e.g., TheOddsApi) supports 
     * the specific league or market requested.
     */
    boolean supports(SportRequest request);

    /**
     * Requirement: Fetch Upcoming Fixtures.
     */
    List<Event> fetchUpcomingFixtures(SportRequest request);

    /**
     * Requirement: Fetch Best Odds.
     */
    Map<String, List<Odds>> fetchBestOdds(SportRequest request);

    /**
     * Requirement: Fetch Scores for Admin review.
     */
    Map<String, Integer[]> fetchScores(SportRequest request);


    public static class SportRequest {
        private final League league;
        private final MarketType marketType;

        public SportRequest(League league, MarketType marketType) {
            this.league = league;
            this.marketType = marketType;
        }

        public League getLeague() {
            return league;
        }

        public MarketType getMarketType() {
            return marketType;
        }
    }
}