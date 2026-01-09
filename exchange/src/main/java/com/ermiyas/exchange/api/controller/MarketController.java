package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.application.service.MarketQueryService;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/markets")
@RequiredArgsConstructor
public class MarketController {

    private final MarketQueryService marketQueryService;

    /**
     * Logic: Groups active fixtures by league for the main Dashboard.
     */
    @GetMapping("/leagues")
    public ResponseEntity<Map<String, List<Event>>> getMarketsByLeague() {
        return ResponseEntity.ok(marketQueryService.getEventsByLeague());
    }

    /**
     * Logic: Returns detailed liquidity and odds for the Match Detail view.
     */
    @GetMapping("/fixtures/{id}")
    public ResponseEntity<Map<String, Object>> getFixtureDetail(@PathVariable Long id) throws ExchangeException {
        return ResponseEntity.ok(marketQueryService.getFixtureDetailSnapshot(id));
    }
}