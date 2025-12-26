package com.ermiyas.exchange.referenceOdds.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class OddsApiClient {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String host;

    public OddsApiClient() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.apiKey = OddsApiConfig.getApiKey();
        this.host = OddsApiConfig.getHost();
    }

    public JsonNode getSports() throws Exception {
        return getJson(host + "/sports?apiKey=" + apiKey);
    }

    public JsonNode getEvents(String sportKey) throws Exception {
        return getJson(host + "/sports/" + sportKey + "/events?apiKey=" + apiKey);
    }

    public JsonNode getEventOdds(String sportKey, String eventId) throws Exception {
        String url = String.format("%s/sports/%s/events/%s/odds?apiKey=%s&regions=eu&markets=h2h&oddsFormat=decimal&dateFormat=iso",
                host, sportKey, eventId, apiKey);
        return getJson(url);
    }

    private JsonNode getJson(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("HTTP " + res.statusCode() + " for: " + url + " -> " + res.body());
        }

        return mapper.readTree(res.body());
    }
}
