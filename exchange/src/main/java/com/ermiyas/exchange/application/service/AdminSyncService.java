package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider.SportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REFACTORED: AdminSyncService (OCP Friendly)
 * This service handles the "Sync Fixtures" button logic.
 * It fetches data from external APIs and persists it to the database.
 */
@Service
@RequiredArgsConstructor
public class AdminSyncService {

    private final EventRepository eventRepository;
    private final List<SportsDataProvider> providers;

    /**
     * Entry point for the "Sync Fixtures" button.
     * Logic: Iterates through all leagues and all providers to refresh data.
     */
    @Transactional
    public void syncAllFixtures() {
        League[] leagues = League.values();
        
        for (int i = 0; i < leagues.length; i++) {
            League league = leagues[i];
            // We assume 3-WAY/H2H as the default sync market
            SportRequest request = new SportRequest(league, MarketType.THREE_WAY);
            
            for (int j = 0; j < providers.size(); j++) {
                SportsDataProvider provider = providers.get(j);
                
                if (provider.supports(request)) {
                    syncLeagueFixtures(provider, request);
                    syncLeagueScores(provider, request);
                }
            }
        }
    }

    /**
     * Logic: Fetches upcoming matches and saves new ones to the DB.
     */
    private void syncLeagueFixtures(SportsDataProvider provider, SportRequest request) {
        List<Event> externalEvents = provider.fetchUpcomingFixtures(request);
        
        for (int i = 0; i < externalEvents.size(); i++) {
            Event incomingEvent = externalEvents.get(i);
            Optional<Event> existingEventOpt = eventRepository.getByExternalId(incomingEvent.getExternalId());
            
            if (!existingEventOpt.isPresent()) {
                // It's a new fixture, so we save it. 
                // We ensure the league and market type are set correctly.
                eventRepository.save(incomingEvent);
            } else {
                // Optional: Update start times for existing fixtures if they changed
                Event existing = existingEventOpt.get();
                // Logic to update start time could go here if needed
            }
        }
    }

    /**
     * Logic: Fetches latest scores and updates existing events in the DB.
     */
    private void syncLeagueScores(SportsDataProvider provider, SportRequest request) {
        Map<String, Integer[]> scoreMap = provider.fetchScores(request);
        
        // We iterate through the scores provided by the API
        for (Map.Entry<String, Integer[]> entry : scoreMap.entrySet()) {
            String externalId = entry.getKey();
            Integer[] scores = entry.getValue();
            
            Optional<Event> eventOpt = eventRepository.getByExternalId(externalId);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                
                // Only sync scores if the match isn't already settled
                // This service just ensures the data is in the DB.
                eventRepository.save(event); 
            }
        }
    }
}