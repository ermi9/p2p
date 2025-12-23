package com.ermiyas.exchange.referenceOdds.infrastructure.oddsapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Low-level HTTP client for The Odds API.
 *
 * Responsibility:
 * - build URLs
 * - execute HTTP requests
 * - return raw JSON
 */
public class OddsApiClient {

    private static final String BASE_URL =
            "https://api.the-odds-api.com/v4";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public OddsApiClient(String apiKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    /**
     * Fetch upcoming events (fixtures) for a league.
     */
 public List<JsonNode> fetchEvents(String leagueKey) {

    String url = BASE_URL
            + "/sports/" + leagueKey
            + "/events"
            + "?apiKey=" + apiKey;

    JsonNode root = getJson(url);

    if (root == null || !root.isArray()) {
        return List.of();
    }

    List<JsonNode> events = new java.util.ArrayList<>();
    for (JsonNode node : root) {
        events.add(node);
    }
    return events;
}


    /**
     * Fetch H2H odds for a specific event.
     */
    public JsonNode fetchEventOdds(String leagueKey, String eventId) {

        String url = BASE_URL
                + "/sports/" + leagueKey
                + "/events/" + eventId
                + "/odds"
                + "?regions=eu"
                + "&markets=h2h"
                + "&oddsFormat=decimal"
                + "&apiKey=" + apiKey;

        return getJson(url);
    }

    /**
     * Execute HTTP GET and parse JSON.
     */
    private JsonNode getJson(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {
                return null;
            }

            return objectMapper.readTree(response.body());

        } catch (Exception e) {
            return null;
        }
    }
}
