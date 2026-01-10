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
import com.ermiyas.exchange.domain.model.MarketType;

import java.util.*;

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
        List<Event> events = new ArrayList<Event>();

        if (response != null) {
            for (int i = 0; i < response.length; i++) {
                TheOddsApiFixtureDto dto = response[i];
                events.add(Event.builder()
                        .externalId(dto.getId())
                        .homeTeam(dto.getHomeTeam())
                        .awayTeam(dto.getAwayTeam())
                        .startTime(dto.getStartTime())
                        .status(EventStatus.OPEN)
                        .league(request.getLeague()) 
                        .marketType(request.getMarketType())
                        .build());
            }
        }
        return events;
    }

    @Override
    public Map<String, BestOddsResult> fetchBestOddsWithSources(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        MarketStrategy strategy = getStrategy(request);
        
        if (strategy == null) {
            return Collections.emptyMap();
        }

        String url = String.format("%s/sports/%s/odds?apiKey=%s&regions=eu&markets=%s", 
                baseUrl, leagueKey, apiKey, strategy.getMarketKey());
        
        TheOddsApiOddsDto[] response = restTemplate.getForObject(url, TheOddsApiOddsDto[].class);
        Map<String, BestOddsResult> resultsMap = new HashMap<String, BestOddsResult>();

        if (response != null) {
            for (int i = 0; i < response.length; i++) {
                TheOddsApiOddsDto dto = response[i];
                // Polymorphic interface call works with Spring Proxies and avoids casting
                BestOddsResult result = strategy.calculateBestOddsWithSources(dto);
                if (result != null) {
                    resultsMap.put(dto.getId(), result);
                }
            }
        }
        return resultsMap;
    }

    @Override
    public Map<String, List<Odds>> fetchBestOdds(SportRequest request) {
        Map<String, BestOddsResult> sourceData = fetchBestOddsWithSources(request);
        Map<String, List<Odds>> legacyMap = new HashMap<String, List<Odds>>();
        
        // Use traditional entry set iteration instead of forEach lambda
        for (Map.Entry<String, BestOddsResult> entry : sourceData.entrySet()) {
            BestOddsResult res = entry.getValue();
            List<Odds> oddsList = new ArrayList<Odds>();
            oddsList.add(res.getHomeOdds());
            oddsList.add(res.getAwayOdds());
            oddsList.add(res.getDrawOdds());
            legacyMap.put(entry.getKey(), oddsList);
        }
        return legacyMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer[]> fetchScores(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        String url = String.format("%s/sports/%s/scores?apiKey=%s&daysFrom=1", baseUrl, leagueKey, apiKey);
        
        Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
        Map<String, Integer[]> scoreMap = new HashMap<String, Integer[]>();

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

    /**
     * Standard Java loop to replace Stream/filter/findFirst
     */
    private MarketStrategy getStrategy(SportRequest request) {
        for (int i = 0; i < marketStrategies.size(); i++) {
            MarketStrategy strategy = marketStrategies.get(i);
            if (strategy.supports(request.getMarketType())) {
                return strategy;
            }
        }
        return null;
    }

    public interface MarketStrategy {
        boolean supports(MarketType type);
        String getMarketKey();
        BestOddsResult calculateBestOddsWithSources(TheOddsApiOddsDto dto);
        List<Odds> calculateBestOdds(TheOddsApiOddsDto dto);
    }
}