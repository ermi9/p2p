package com.ermiyas.exchange.referenceOdds.application;

import com.ermiyas.exchange.referenceOdds.domain.ExternalLeagueType;
import com.ermiyas.exchange.referenceOdds.domain.ReferenceOddsService;
import com.ermiyas.exchange.referenceOdds.infrastructure.external.ExternalOddsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceOddsCrawler {
    private final ExternalOddsClient client;
    private final ReferenceOddsService referenceOddsService;

    // Reduced to 30 seconds (30000ms) for active testing
    @Scheduled(fixedRate = 30000) 
    public void run() {
        log.info("Starting Reference Odds Crawl...");
        
        for (ExternalLeagueType league : ExternalLeagueType.values()) {
            try {
                log.info("Fetching odds for league: {}", league.getApiCode());
                var externalMatches = client.fetchLiveOdds(league.getApiCode());
                
                if (externalMatches == null || externalMatches.isEmpty()) {
                    log.warn("No matches returned from API for league: {}", league.getApiCode());
                    continue;
                }

                externalMatches.forEach(matchDto -> {
                    referenceOddsService.ingestAndLinkOdds(matchDto);
                });
                
            } catch (Exception e) {
                log.error("Error crawling league {}: {}", league.getApiCode(), e.getMessage());
            }
        }
        log.info("Crawl cycle completed.");
    }
}