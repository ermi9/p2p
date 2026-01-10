package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.api.dto.ExchangeDtos.EventSummaryResponse;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketQueryService {

    private final EventRepository eventRepository;
    private final OfferRepository offerRepository;
    private final BetRepository betRepository;

    /**
     * UPDATED: Returns DTOs instead of raw Entities.
     * Logic: Groups active fixtures by league and includes professional branding.
     */
    public Map<String, List<EventSummaryResponse>> getEventsByLeague() {
        List<Event> openEvents = eventRepository.findAllByStatus(EventStatus.OPEN);
        Map<String, List<EventSummaryResponse>> leagueMap = new HashMap<>();

        for (Event event : openEvents) {
            String leagueDisplayName = (event.getLeague() != null) 
                    ? event.getLeague().getDisplayName() 
                    : "International Football";

            // Map the Entity to the DTO including the NEW source fields
            EventSummaryResponse summary = mapToSummary(event);

            leagueMap.computeIfAbsent(leagueDisplayName, k -> new ArrayList<>()).add(summary);
        }
        return leagueMap;
    }

    /**
     * Provides a snapshot including the Bookmaker Source for each odd.
     */
    public Map<String, Object> getFixtureDetailSnapshot(Long eventId) throws ExchangeException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new UserNotFoundException("Market Error: Fixture #" + eventId + " not found."));

        Map<String, Object> snapshot = new HashMap<>();
        
        snapshot.put("id", event.getId());
        snapshot.put("homeTeam", event.getHomeTeam());
        snapshot.put("awayTeam", event.getAwayTeam());
        snapshot.put("startTime", event.getStartTime());

        // Grouping price with its source
        Map<String, Object> homePrice = new HashMap<>();
        homePrice.put("odds", event.getRefHomeOdds().value().doubleValue());
        homePrice.put("source", event.getRefHomeSource()); // New field
        
        Map<String, Object> awayPrice = new HashMap<>();
        awayPrice.put("odds", event.getRefAwayOdds().value().doubleValue());
        awayPrice.put("source", event.getRefAwaySource()); // New field
        
        Map<String, Object> drawPrice = new HashMap<>();
        drawPrice.put("odds", event.getRefDrawOdds().value().doubleValue());
        drawPrice.put("source", event.getRefDrawSource()); // New field

        Map<String, Object> bookiePrices = new HashMap<>();
        bookiePrices.put("HOME", homePrice);
        bookiePrices.put("AWAY", awayPrice);
        bookiePrices.put("DRAW", drawPrice);
        
        snapshot.put("referenceOdds", bookiePrices);
        snapshot.put("exchangeLiquidity", aggregateP2PLiquidity(eventId));

        return snapshot;
    }

    /**
     * Helper Logic: Maps the Entity to a DTO for the API layer.
     */
    private EventSummaryResponse mapToSummary(Event event) {
        return EventSummaryResponse.builder()
                .id(event.getId())
                .homeTeam(event.getHomeTeam())
                .awayTeam(event.getAwayTeam())
                .startTime(event.getStartTime())
                .leagueName(event.getLeague() != null ? event.getLeague().getDisplayName() : null)
                // Values
                .homeOdds(event.getRefHomeOdds() != null ? event.getRefHomeOdds().value().doubleValue() : null)
                .awayOdds(event.getRefAwayOdds() != null ? event.getRefAwayOdds().value().doubleValue() : null)
                .drawOdds(event.getRefDrawOdds() != null ? event.getRefDrawOdds().value().doubleValue() : null)
                // Sources (The "Professional" Brand Names)
                .homeSource(event.getRefHomeSource())
                .awaySource(event.getRefAwaySource())
                .drawSource(event.getRefDrawSource())
                .build();
    }

    //  logic below remains unchanged 

    public List<Offer> getUserOpenOffers(Long userId) {
        List<Offer> all = offerRepository.findAll();
        List<Offer> userOffers = new ArrayList<>();
        for (Offer offer : all) {
            if (offer.getMaker().getId().equals(userId) && 
               (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN)) {
                userOffers.add(offer);
            }
        }
        return userOffers;
    }

    public List<Bet> getUserMatchedBets(Long userId) {
        List<Bet> all = betRepository.findAll();
        List<Bet> userBets = new ArrayList<>();
        for (Bet bet : all) {
            if (bet.getTaker().getId().equals(userId) || bet.getOffer().getMaker().getId().equals(userId)) {
                userBets.add(bet);
            }
        }
        return userBets;
    }

    private Map<Outcome, Map<Double, Double>> aggregateP2PLiquidity(Long eventId) {
        List<Offer> allOffers = offerRepository.findAllByEventId(eventId);
        Map<Outcome, Map<Double, Double>> liquidity = new HashMap<>();

        for (Offer offer : allOffers) {
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                Outcome outcome = offer.getPredictedOutcome();
                Double oddsValue = offer.getOdds().value().doubleValue();
                Double availableStake = offer.getRemainingStake().value().doubleValue();

                liquidity.computeIfAbsent(outcome, k -> new HashMap<>());
                Map<Double, Double> priceBuckets = liquidity.get(outcome);
                priceBuckets.put(oddsValue, priceBuckets.getOrDefault(oddsValue, 0.0) + availableStake);
            }
        }
        return liquidity;
    }
}