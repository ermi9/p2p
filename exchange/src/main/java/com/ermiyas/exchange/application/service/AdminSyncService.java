package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.logic.SettlementStrategyFactory;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.infrastructure.sports.BestOddsResult;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider.SportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSyncService {

    private final EventRepository eventRepository;
    private final List<SportsDataProvider> providers;
    private final SettlementStrategyFactory strategyFactory;

    public long getActiveFixtureCount() {
        return eventRepository.count();
    }

    /**
     * Refactored Sync Loop
     */
    @Scheduled(fixedRate = 10800000) // 3 Hours
    public void syncAllFixtures() {
        League[] leagues = League.values();
        for (int i = 0; i < leagues.length; i++) {
            League league = leagues[i];
            SportRequest request = new SportRequest(league, MarketType.THREE_WAY);
            
            for (int j = 0; j < providers.size(); j++) {
                SportsDataProvider provider = providers.get(j);
                if (provider.supports(request)) {
                    try {
                        // Spacing out requests to avoid "Too Many Requests" (429) errors
                        syncLeagueFixtures(provider, request);
                        pause(); 
                        
                        syncLeagueOdds(provider, request);
                        pause();
                        
                        syncLeagueScores(provider, request);
                        pause();
                        
                    } catch (Exception e) {
                        // Log error but continue with the next league/provider
                        System.err.println("Sync failed for league " + league + " using " + provider.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void pause() {
        try {
            // 1-second delay between API calls to stay within frequency limits
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

    private void syncLeagueOdds(SportsDataProvider provider, SportRequest request) {
        Map<String, BestOddsResult> oddsResultMap = provider.fetchBestOddsWithSources(request);
        for (Map.Entry<String, BestOddsResult> entry : oddsResultMap.entrySet()) {
            Optional<Event> eventOpt = eventRepository.getByExternalId(entry.getKey());
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                BestOddsResult result = entry.getValue();
                event.setRefHomeOdds(result.getHomeOdds());
                event.setRefAwayOdds(result.getAwayOdds());
                event.setRefDrawOdds(result.getDrawOdds());
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
                        //using dynamic dispatch + Factory pattern
                        SettlementStrategy strategy=strategyFactory.getStrategy(event.getMarketType());
                        
                        // Triggers transition to COMPLETED state
                        event.processResult(scores[0], scores[1], strategy);
                        eventRepository.save(event);
                    } catch (Exception e) {
                        System.err.println("Failed to process background result for event: " + event.getId());
                    }
                }
            }
        }
    }
}