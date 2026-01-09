package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.application.service.MarketQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final MarketQueryService queryService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMyActivity(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of(
            "openOffers", queryService.getUserOpenOffers(userId),
            "matchedBets", queryService.getUserMatchedBets(userId)
        ));
    }
}