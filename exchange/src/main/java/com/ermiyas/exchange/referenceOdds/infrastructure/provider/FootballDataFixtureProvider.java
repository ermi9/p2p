package com.ermiyas.exchange.referenceOdds.infrastructure.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.League;
import com.ermiyas.exchange.referenceOdds.infrastructure.FixtureHttpClient;
import com.ermiyas.exchange.referenceOdds.infrastructure.config.FixtureApiConfig;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FootballDataFixtureProvider implements FixtureProvider {
    private final FixtureHttpClient httpClient;
    private final FixtureApiConfig config;
    private final ObjectMapper mapper;

    public FootballDataFixtureProvider(FixtureHttpClient httpClient, FixtureApiConfig config, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.config = config;
        this.mapper = mapper;
    }

    @Override
    public List<Fixture> fetchFixtures(League league) {
        try {
            // v4 API requires specific date ranges to show upcoming matches
            String dateFrom = java.time.LocalDate.now().toString();
            String dateTo = java.time.LocalDate.now().plusDays(14).toString();

            // Correct URL structure for v4
            String url = config.getFixtureUrl() + "/competitions/" + league.name() + 
                         "/matches?dateFrom=" + dateFrom + "&dateTo=" + dateTo;
            
            System.out.println("Calling ACTUAL API: " + url);

            String response = httpClient.get(url, "X-Auth-Token", config.getFixtureKey());
            JsonNode root = mapper.readTree(response);

            List<Fixture> fixtures = new ArrayList<>();
            
            // In v4, matches is the root array
            if (root.has("matches") && root.get("matches").isArray()) {
                root.get("matches").forEach(node -> {
                    fixtures.add(new Fixture(
                        node.get("id").asText(),
                        // IMPORTANT: v4 nesting is node -> homeTeam -> name
                        node.get("homeTeam").get("name").asText(), 
                        node.get("awayTeam").get("name").asText(),
                        ZonedDateTime.parse(node.get("utcDate").asText()).toLocalDateTime(),
                        league
                    ));
                });
            } else {
                System.out.println("Warning: No matches found in API response for " + league);
            }
            
            return fixtures;
        } catch (Exception e) {
            System.err.println("CRITICAL API ERROR: " + e.getMessage());
            return List.of(); 
        }
    }

    @Override
    public String getName() { return "FootballDataOrg"; }
}