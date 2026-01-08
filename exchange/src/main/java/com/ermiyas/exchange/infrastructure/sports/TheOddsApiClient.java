package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.EventStatus;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiFixtureDto;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * TheOddsApiClient (OCP Friendly)
 */
@Component
@RequiredArgsConstructor
public class TheOddsApiClient implements SportsDataProvider {

    private final RestTemplate restTemplate;
    private final List<MarketStrategy> marketStrategies;
    
    @Value("${api.theodds.key}")
    private String apiKey;

    @Value("${api.theodds.base-url}")
    private String baseUrl;

    @Override
    public boolean supports(SportRequest request) {
        return request.getLeague() != null && getStrategy(request) != null;
    }

    @Override
    public List<Event> fetchUpcomingFixtures(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        String url = String.format("%s/sports/%s/events?apiKey=%s", baseUrl, leagueKey, apiKey);
        
        TheOddsApiFixtureDto[] response = restTemplate.getForObject(url, TheOddsApiFixtureDto[].class);
        List<Event> events = new ArrayList<>();

        if (response != null) {
            for (int i = 0; i < response.length; i++) {
                TheOddsApiFixtureDto dto = response[i];
                events.add(Event.builder()
                        .externalId(dto.getId())
                        .homeTeam(dto.getHomeTeam())
                        .awayTeam(dto.getAwayTeam())
                        .startTime(dto.getStartTime())
                        .status(EventStatus.OPEN)
                        .build());
            }
        }
        return events;
    }

    @Override
    public Map<String, List<Odds>> fetchBestOdds(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        MarketStrategy strategy = getStrategy(request);
        
        String url = String.format("%s/sports/%s/odds?apiKey=%s&regions=eu&markets=%s", 
                baseUrl, leagueKey, apiKey, strategy.getMarketKey());
        
        TheOddsApiOddsDto[] response = restTemplate.getForObject(url, TheOddsApiOddsDto[].class);
        Map<String, List<Odds>> bestOddsMap = new HashMap<>();

        if (response != null) {
            for (int i = 0; i < response.length; i++) {
                TheOddsApiOddsDto dto = response[i];
                List<Odds> processedOdds = strategy.calculateBestOdds(dto);
                bestOddsMap.put(dto.getId(), processedOdds);
            }
        }
        return bestOddsMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer[]> fetchScores(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        String url = String.format("%s/sports/%s/scores?apiKey=%s&daysFrom=1", baseUrl, leagueKey, apiKey);
        
        Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
        Map<String, Integer[]> scoreMap = new HashMap<>();

        if (response != null) {
            for (int i = 0; i < response.length; i++) {
                Map<String, Object> event = response[i];
                List<Map<String, Object>> scores = (List<Map<String, Object>>) event.get("scores");
                
                if (scores != null && scores.size() >= 2) {
                    Integer h = Integer.parseInt(scores.get(0).get("score").toString());
                    Integer a = Integer.parseInt(scores.get(1).get("score").toString());
                    scoreMap.put(event.get("id").toString(), new Integer[]{h, a});
                }
            }
        }
        return scoreMap;
    }

    private MarketStrategy getStrategy(SportRequest request) {
        for (MarketStrategy strategy : marketStrategies) {
            if (strategy.supports(request.getMarketType())) {
                return strategy;
            }
        }
        return null;
    }

    public interface MarketStrategy {
        boolean supports(com.ermiyas.exchange.domain.model.MarketType type);
        String getMarketKey();
        List<Odds> calculateBestOdds(TheOddsApiOddsDto dto);
    }
}