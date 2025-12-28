package com.ermiyas.exchange.referenceOdds.infrastructure.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExternalOddsClient {

    private final RestTemplate restTemplate;

    @Value("${api.theodds.key}")
    private String apiKey;

    @Value("${api.theodds.base-url}")
    private String baseUrl;

    public List<TheOddsApiDto> fetchLiveOdds(String sportApiCode) {
        // Format: baseUrl/sport/odds/?apiKey=...
        String url = String.format("%s/%s/odds/?apiKey=%s&regions=eu&markets=h2h", 
                                    baseUrl, sportApiCode, apiKey);
        
        TheOddsApiDto[] response = restTemplate.getForObject(url, TheOddsApiDto[].class);
        return response != null ? List.of(response) : List.of();
    }
}