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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TheOddsApiClient implements SportsDataProvider {

    private final RestTemplate restTemplate;
    
    @Value("${api.theodds.key}")
    private String apiKey;

    @Value("${api.theodds.base-url}")
    private String baseUrl;

    /**
     * Requirement: Fetch Upcoming Fixtures.
     * Maps API DTOs to internal Event entities.
     */
    @Override
    public List<Event> fetchUpcomingFixtures(String league) {
        String url = String.format("%s/sports/%s/events?apiKey=%s", baseUrl, league, apiKey);
        TheOddsApiFixtureDto[] response = restTemplate.getForObject(url, TheOddsApiFixtureDto[].class);

        if (response == null) return Collections.emptyList();

        return Arrays.stream(response)
                .map(dto -> Event.builder()
                        .externalId(dto.getId())
                        .homeTeam(dto.getHomeTeam())
                        .awayTeam(dto.getAwayTeam())
                        .startTime(dto.getStartTime())
                        .status(EventStatus.OPEN)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Requirement: Fetch Best Odds.
     * Logic: Iterates through all bookmakers to find the highest price for H/A/D.
     */
    @Override
    public Map<String, List<Odds>> fetchBestOdds(String league) {
        String url = String.format("%s/sports/%s/odds?apiKey=%s&regions=eu&markets=h2h", baseUrl, league, apiKey);
        TheOddsApiOddsDto[] response = restTemplate.getForObject(url, TheOddsApiOddsDto[].class);

        Map<String, List<Odds>> bestOddsMap = new HashMap<>();
        if (response == null) return bestOddsMap;

        for (TheOddsApiOddsDto dto : response) {
            double bestHome = 0, bestAway = 0, bestDraw = 0;

            for (TheOddsApiOddsDto.Bookmaker bm : dto.getBookmakers()) {
                for (TheOddsApiOddsDto.Market mkt : bm.getMarkets()) {
                    for (TheOddsApiOddsDto.Outcome outcome : mkt.getOutcomes()) {
                        if (outcome.getName().equals(dto.getHome_team())) 
                            bestHome = Math.max(bestHome, outcome.getPrice());
                        else if (outcome.getName().equals(dto.getAway_team())) 
                            bestAway = Math.max(bestAway, outcome.getPrice());
                        else 
                            bestDraw = Math.max(bestDraw, outcome.getPrice());
                    }
                }
            }
            
            bestOddsMap.put(dto.getId(), List.of(
                new Odds(BigDecimal.valueOf(bestHome)),
                new Odds(BigDecimal.valueOf(bestAway)),
                new Odds(BigDecimal.valueOf(bestDraw))
            ));
        }
        return bestOddsMap;
    }

    /**
     * Requirement: Fetch Scores for Admin review.
     * Returns Integer array: [homeScore, awayScore]
     */
    @Override
    public Map<String, Integer[]> fetchScores(String league) {
        String url = String.format("%s/sports/%s/scores?apiKey=%s&daysFrom=1", baseUrl, league, apiKey);
        // Using a generic Map for scores since it's highly dynamic on free tier
        Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);

        Map<String, Integer[]> scoreMap = new HashMap<>();
        if (response == null) return scoreMap;

        for (Map<String, Object> event : response) {
            List<Map<String, Object>> scores = (List<Map<String, Object>>) event.get("scores");
            if (scores != null && scores.size() >= 2) {
                Integer h = Integer.parseInt(scores.get(0).get("score").toString());
                Integer a = Integer.parseInt(scores.get(1).get("score").toString());
                scoreMap.put(event.get("id").toString(), new Integer[]{h, a});
            }
        }
        return scoreMap;
    }
}