package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PURE OOP: Market Query Service (Read-Only).
 * Provides the data structure for the betting UI: Categorized lists and match details.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketQueryService {

    private final EventRepository eventRepository;
    private final OfferRepository offerRepository;

    /**
     * Logic: Groups all active fixtures by their League display name.
     * This creates the nested list layout (e.g., Premier League -> [Match 1, Match 2]).
     */
    public Map<String, List<Event>> getEventsByLeague() {
        // Fetch only the events currently open for betting
        List<Event> openEvents = eventRepository.findAllByStatus(EventStatus.OPEN);
        Map<String, List<Event>> leagueMap = new HashMap<>();

        for (Event event : openEvents) {
            String leagueDisplayName = "International Football"; // 

            // Logic: Iterate through our League enum to find the match
            // We assume the Event entity stores its league name or API key
            for (League league : League.values()) {
                leagueDisplayName = league.getDisplayName(); 
            }

            // Standard Map logic: Create the list if this league hasn't been seen yet
            if (!leagueMap.containsKey(leagueDisplayName)) {
                leagueMap.put(leagueDisplayName, new ArrayList<Event>());
            }
            leagueMap.get(leagueDisplayName).add(event);
        }
        return leagueMap;
    }

    /**
     * Logic: Provides a complete snapshot of a fixture for the Detail View.
     * Includes teams, start time, reference odds (Bookies), and P2P liquidity (Exchange).
     */
    public Map<String, Object> getFixtureDetailSnapshot(Long eventId) throws ExchangeException {
        // 1. Fetch the Event to get the "Reference Odds" (Bet365, etc.)
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new UserNotFoundException("Market Error: Fixture #" + eventId + " not found."));

        Map<String, Object> snapshot = new HashMap<>();
        
        // Identity Data
        snapshot.put("homeTeam", event.getHomeTeam());
        snapshot.put("awayTeam", event.getAwayTeam());
        snapshot.put("startTime", event.getStartTime());

        // 2. Reference Odds View (The "Bookie" side seen in your image)
        Map<String, Double> bookiePrices = new HashMap<>();
        bookiePrices.put("HOME", event.getRefHomeOdds().value().doubleValue());
        bookiePrices.put("AWAY", event.getRefAwayOdds().value().doubleValue());
        bookiePrices.put("DRAW", event.getRefDrawOdds().value().doubleValue());
        snapshot.put("referenceOdds", bookiePrices);

        // 3. Exchange View (The aggregate of all P2P user offers)
        snapshot.put("exchangeLiquidity", aggregateP2PLiquidity(eventId));

        return snapshot;
    }

    /**
     * Internal Logic: Groups all active user offers into Price Points (Odds vs. Total Money).
     * This is what shows users how much they can bet at specific odds.
     */
    private Map<Outcome, Map<Double, Double>> aggregateP2PLiquidity(Long eventId) {
        List<Offer> allOffers = offerRepository.findAllByEventId(eventId);
        Map<Outcome, Map<Double, Double>> liquidity = new HashMap<>();

        // Plain Java loop to calculate market depth
        for (Offer offer : allOffers) {
            // Integrity Check: Only count money from offers that are not yet filled or cancelled
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                
                Outcome outcome = offer.getPredictedOutcome();
                Double oddsValue = offer.getOdds().value().doubleValue();
                Double availableStake = offer.getRemainingStake().value().doubleValue();

                // Grouping by Outcome
                if (!liquidity.containsKey(outcome)) {
                    liquidity.put(outcome, new HashMap<Double, Double>());
                }

                Map<Double, Double> priceBuckets = liquidity.get(outcome);
                
                // Grouping by Price (Odds)
                if (priceBuckets.containsKey(oddsValue)) {
                    priceBuckets.put(oddsValue, priceBuckets.get(oddsValue) + availableStake);
                } else {
                    priceBuckets.put(oddsValue, availableStake);
                }
            }
        }
        return liquidity;
    }
}