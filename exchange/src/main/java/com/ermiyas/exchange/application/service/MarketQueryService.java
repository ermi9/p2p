package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.api.dto.ExchangeDtos.*;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
     *  
     * 1. Future 'OPEN' matches are returned for players.
     * 2. 'COMPLETED' matches are ONLY returned if they have bets (for Admin settlement).
     */
    public Map<String, List<EventSummaryResponse>> getEventsByLeague() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allEvents = eventRepository.findAll();
        Map<String, List<EventSummaryResponse>> leagueMap = new HashMap<>();
        
        for (int i = 0; i < allEvents.size(); i++) {
            Event event = allEvents.get(i);
            
            // Temporal and Status Check
            boolean isFutureOpen = (event.getStatus() == EventStatus.OPEN && event.getStartTime().isAfter(now));
            
            //  Only send COMPLETED matches to the UI if there is something to settle
            boolean isCompletedWithBets = (event.getStatus() == EventStatus.COMPLETED && 
                                          event.getOffers() != null && !event.getOffers().isEmpty());

            if (isFutureOpen || isCompletedWithBets) {
                String leagueName = (event.getLeague() != null) ? event.getLeague().getDisplayName() : "International Football";
                
                if (!leagueMap.containsKey(leagueName)) {
                    leagueMap.put(leagueName, new ArrayList<>());
                }
                leagueMap.get(leagueName).add(mapToSummary(event));
            }
        }
        return leagueMap;
    }

    public Map<String, Object> getFixtureDetailSnapshot(Long eventId) throws ExchangeException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new UserNotFoundException("Fixture #" + eventId + " not found."));
        
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("id", event.getId());
        snapshot.put("homeTeam", event.getHomeTeam());
        snapshot.put("awayTeam", event.getAwayTeam());
        snapshot.put("startTime", event.getStartTime());
        
        snapshot.put("homeOdds", event.getRefHomeOdds().value());
        snapshot.put("homeSource", event.getRefHomeSource());
        snapshot.put("awayOdds", event.getRefAwayOdds().value());
        snapshot.put("awaySource", event.getRefAwaySource());
        snapshot.put("drawOdds", event.getRefDrawOdds().value());
        snapshot.put("drawSource", event.getRefDrawSource());

        List<Offer> offers = offerRepository.findAllByEventId(eventId);
        List<OfferResponse> offerResponses = new ArrayList<>();
        for (int j = 0; j < offers.size(); j++) {
            offerResponses.add(mapToOfferResponse(offers.get(j)));
        }
        snapshot.put("offers", offerResponses);
        return snapshot;
    }

    public List<OfferResponse> getUserOpenOffers(Long userId) {
        List<Offer> all = offerRepository.findAll();
        List<OfferResponse> results = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            Offer o = all.get(i);
            if (o.getMaker() != null && o.getMaker().getId().equals(userId)) {
                if (o.getStatus() == OfferStatus.OPEN || o.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                    results.add(mapToOfferResponse(o));
                }
            }
        }
        return results;
    }

    public List<MatchedBetResponse> getUserMatchedBets(Long userId) {
        List<Bet> all = betRepository.findAll();
        List<MatchedBetResponse> results = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            Bet b = all.get(i);
            if (b.getStatus() == BetStatus.MATCHED || b.getStatus() == BetStatus.SETTLED) {
                boolean isTaker = (b.getTaker() != null && b.getTaker().getId().equals(userId));
                boolean isMaker = (b.getOffer() != null && b.getOffer().getMaker() != null && b.getOffer().getMaker().getId().equals(userId));
                
                if (isTaker || isMaker) {
                    results.add(mapToBetResponse(b));
                }
            }
        }
        return results;
    }

    private MatchedBetResponse mapToBetResponse(Bet b) {
        return MatchedBetResponse.builder()
                .id(b.getId())
                .status(b.getStatus().name())
                .offer(mapToOfferResponse(b.getOffer()))
                .taker(UserResponse.builder().id(b.getTaker().getId()).username(b.getTaker().getUsername()).build())
                .takerLiability(b.getTakerLiability().value())
                .makerStake(b.getMakerStake().value())
                .odds(b.getOdds().value())
                .build();
    }

    private EventSummaryResponse mapToSummary(Event event) {
        return EventSummaryResponse.builder()
                .id(event.getId())
                .externalId(event.getExternalId())
                .homeTeam(event.getHomeTeam())
                .awayTeam(event.getAwayTeam())
                .startTime(event.getStartTime())
                .leagueName(event.getLeague() != null ? event.getLeague().getDisplayName() : "International Football")
                .homeOdds(event.getRefHomeOdds() != null ? event.getRefHomeOdds().value().doubleValue() : null)
                .awayOdds(event.getRefAwayOdds() != null ? event.getRefAwayOdds().value().doubleValue() : null)
                .drawOdds(event.getRefDrawOdds() != null ? event.getRefDrawOdds().value().doubleValue() : null)
                .homeSource(event.getRefHomeSource())
                .awaySource(event.getRefAwaySource())
                .drawSource(event.getRefDrawSource())
                .status(event.getStatus() != null ? event.getStatus().name() : null)
                .offerCount(event.getOffers() != null ? event.getOffers().size() : 0)
                .finalHomeScore(event.getFinalHomeScore())
                .finalAwayScore(event.getFinalAwayScore())
                .build();
    }

    private OfferResponse mapToOfferResponse(Offer o) {
        return OfferResponse.builder()
                .id(o.getId())
                .maker(UserResponse.builder()
                        .id(o.getMaker().getId())
                        .username(o.getMaker().getUsername())
                        .role(o.getMaker().getRoleName())
                        .build())
                .event(mapToEventResponse(o.getEvent()))
                .outcome(o.getPredictedOutcome() != null ? o.getPredictedOutcome().name() : null)
                .odds(o.getOdds() != null ? o.getOdds().value() : null)
                .remainingStake(o.getRemainingStake() != null ? o.getRemainingStake().value() : null)
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .build();
    }

    private EventResponse mapToEventResponse(Event e) {
        return EventResponse.builder()
                .id(e.getId())
                .homeTeam(e.getHomeTeam())
                .awayTeam(e.getAwayTeam())
                .startTime(e.getStartTime())
                .leagueName(e.getLeague() != null ? e.getLeague().getDisplayName() : "International Football")
                .build();
    }
}