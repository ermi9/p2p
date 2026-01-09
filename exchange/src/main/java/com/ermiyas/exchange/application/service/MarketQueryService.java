package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepository; // New Import
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
    private final BetRepository betRepository; // Added for Activity queries

    /**
     * Logic: Groups all active fixtures by their League display name.
     * Fixed: Now uses the league assigned to the Event instead of a loop.
     */
    public Map<String, List<Event>> getEventsByLeague() {
        List<Event> openEvents = eventRepository.findAllByStatus(EventStatus.OPEN);
        Map<String, List<Event>> leagueMap = new HashMap<>();

        for (Event event : openEvents) {
            // Logic: Correctly resolve the display name from the event entity
            String leagueDisplayName = (event.getLeague() != null) 
                    ? event.getLeague().getDisplayName() 
                    : "International Football";

            // Standard Map logic: Use computeIfAbsent for cleaner code
            leagueMap.computeIfAbsent(leagueDisplayName, k -> new ArrayList<>()).add(event);
        }
        return leagueMap;
    }

    /**
     * Logic: Provides a complete snapshot of a fixture for the Detail View.
     */
    public Map<String, Object> getFixtureDetailSnapshot(Long eventId) throws ExchangeException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new UserNotFoundException("Market Error: Fixture #" + eventId + " not found."));

        Map<String, Object> snapshot = new HashMap<>();
        
        snapshot.put("homeTeam", event.getHomeTeam());
        snapshot.put("awayTeam", event.getAwayTeam());
        snapshot.put("startTime", event.getStartTime());

        Map<String, Double> bookiePrices = new HashMap<>();
        bookiePrices.put("HOME", event.getRefHomeOdds().value().doubleValue());
        bookiePrices.put("AWAY", event.getRefAwayOdds().value().doubleValue());
        bookiePrices.put("DRAW", event.getRefDrawOdds().value().doubleValue());
        snapshot.put("referenceOdds", bookiePrices);

        snapshot.put("exchangeLiquidity", aggregateP2PLiquidity(eventId));

        return snapshot;
    }

    /**
     * Activity Logic: Returns active offers posted by the user (Liquidity they provided).
     */
    public List<Offer> getUserOpenOffers(Long userId) {
        // Fetches all offers and filters for those still active in the market
        return offerRepository.findAll().stream()
                .filter(offer -> offer.getMaker().getId().equals(userId))
                .filter(offer -> offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN)
                .toList();
    }

    /**
     * Activity Logic: Returns matched bets for the user.
     */
    public List<Bet> getUserMatchedBets(Long userId) {
        // Fetches matched bets where the user is either the Maker or the Taker
        return betRepository.findAll().stream()
                .filter(bet -> bet.getTaker().getId().equals(userId) || bet.getOffer().getMaker().getId().equals(userId))
                .toList();
    }

    /**
     * Internal Logic: Groups active user offers into Price Points for the UI ladder.
     */
    private Map<Outcome, Map<Double, Double>> aggregateP2PLiquidity(Long eventId) {
        List<Offer> allOffers = offerRepository.findAllByEventId(eventId);
        Map<Outcome, Map<Double, Double>> liquidity = new HashMap<>();

        for (Offer offer : allOffers) {
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                
                Outcome outcome = offer.getPredictedOutcome();
                Double oddsValue = offer.getOdds().value().doubleValue();
                Double availableStake = offer.getRemainingStake().value().doubleValue();

                if (!liquidity.containsKey(outcome)) {
                    liquidity.put(outcome, new HashMap<Double, Double>());
                }

                Map<Double, Double> priceBuckets = liquidity.get(outcome);
                
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