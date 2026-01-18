package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.infrastructure.sports.BestOddsResult;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider.SportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSyncService {

    private final EventRepository eventRepository;
    private final List<SportsDataProvider> providers;
    private final SettlementStrategy settlementStrategy;

    @Transactional
    public void syncAllFixtures() {
        League[] leagues = League.values();
        for (int i = 0; i < leagues.length; i++) {
            League league = leagues[i];
            SportRequest request = new SportRequest(league, MarketType.THREE_WAY);
            
            for (int j = 0; j < providers.size(); j++) {
                SportsDataProvider provider = providers.get(j);
                if (provider.supports(request)) {
                    // LOGGING: Check which provider is running
                    System.out.println("SYNC: Starting sync for league " + league + " using provider " + provider.getClass().getSimpleName());
                    
                    syncLeagueFixtures(provider, request);
                    syncLeagueOdds(provider, request); 
                    syncLeagueScores(provider, request);
                }
            }
        }
    }

    private void syncLeagueFixtures(SportsDataProvider provider, SportRequest request) {
        List<Event> externalEvents = provider.fetchUpcomingFixtures(request);
        for (int i = 0; i < externalEvents.size(); i++) {
            Event incomingEvent = externalEvents.get(i);
            Optional<Event> existingEventOpt = eventRepository.getByExternalId(incomingEvent.getExternalId());
            if (!existingEventOpt.isPresent()) {
                eventRepository.save(incomingEvent);
            }
        }
    }

    /**
     * Standard Java implementation of odds synchronization.
     */
    private void syncLeagueOdds(SportsDataProvider provider, SportRequest request) {
        Map<String, BestOddsResult> oddsResultMap = provider.fetchBestOddsWithSources(request);
        
        // LOGGING: Confirm if the infrastructure actually returned data
        System.out.println("DEBUG: Odds map size for " + request.getLeague() + ": " + oddsResultMap.size());

        for (Map.Entry<String, BestOddsResult> entry : oddsResultMap.entrySet()) {
            Optional<Event> eventOpt = eventRepository.getByExternalId(entry.getKey());
            
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                BestOddsResult result = entry.getValue();
                
                // LOGGING: Verify source is not null before saving
                if (result.getHomeSource() == null) {
                    System.out.println("WARNING: Provider returned NULL source for event ID: " + entry.getKey());
                }

                // Set Odds
                event.setRefHomeOdds(result.getHomeOdds());
                event.setRefAwayOdds(result.getAwayOdds());
                event.setRefDrawOdds(result.getDrawOdds());

                // Set Sources
                event.setRefHomeSource(result.getHomeSource());
                event.setRefAwaySource(result.getAwaySource());
                event.setRefDrawSource(result.getDrawSource());

                eventRepository.save(event);
            }
        }
    }

    private void syncLeagueScores(SportsDataProvider provider, SportRequest request) {
        Map<String, Integer[]> scoreMap = provider.fetchScores(request);
        
        for (Map.Entry<String, Integer[]> entry : scoreMap.entrySet()) {
            Optional<Event> eventOpt = eventRepository.getByExternalId(entry.getKey());
            
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                Integer[] scores = entry.getValue();
                
                if (scores != null && scores.length >= 2) {
                    try {
                        // Call processResult instead of determineWinner
                        // and pass the settlementStrategy
                        event.processResult(scores[0], scores[1], settlementStrategy);
                        eventRepository.save(event);
                    } catch (Exception e) {
                        System.err.println("Failed to process result for event " + event.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}