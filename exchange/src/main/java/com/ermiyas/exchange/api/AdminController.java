package com.ermiyas.exchange.api;

import com.ermiyas.exchange.application.AdminSyncService;
import com.ermiyas.exchange.application.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Admin actions.
 * Provides endpoints for manual refreshing of data and final settlement.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminSyncService adminSyncService;
    private final SettlementService settlementService;

    // Defined based on your requirement for Europe's Top 5 Leagues
    private static final List<String> TOP_5_LEAGUES = List.of(
            "soccer_epl",                  // English Premier League
            "soccer_spain_la_liga",        // La Liga
            "soccer_germany_bundesliga",   // Bundesliga
            "soccer_italy_serie_a",        // Serie A
            "soccer_france_ligue_one"      // Ligue 1
    );

    /**
     * Button: "Refresh Fixtures"
     * Fetches match names/teams and saves them to the DB.
     */
    @PostMapping("/sync-fixtures")
    public ResponseEntity<String> syncFixtures() {
        adminSyncService.syncLeagues(TOP_5_LEAGUES);
        return ResponseEntity.ok("Successfully synced fixtures for Top 5 Leagues.");
    }

    /**
     * Button: "Refresh Reference Odds"
     * Fetches the best available H2H odds and updates existing events.
     */
    @PostMapping("/refresh-odds")
    public ResponseEntity<String> refreshOdds() {
        adminSyncService.refreshOdds(TOP_5_LEAGUES);
        return ResponseEntity.ok("Reference odds have been updated from external API.");
    }

    /**
     * Button: "Refresh Scores"
     * Fetches current scores for completed or live matches.
     */
    @PostMapping("/refresh-scores")
    public ResponseEntity<String> refreshScores() {
        adminSyncService.refreshScores(TOP_5_LEAGUES);
        return ResponseEntity.ok("Latest scores fetched and saved to database.");
    }

    /**
     * Button: "Settle Match"
     * Triggers the transfer of funds between Maker and Taker based on the final result.
     * Note: In a student project, you can pass 0,0 here if the scores are already in the DB,
     * or use these params to override.
     */
    @PostMapping("/settle/{eventId}")
    public ResponseEntity<String> settleMatch(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int homeScore,
            @RequestParam(defaultValue = "0") int awayScore) {
        
        // This moves the money based on the Outcome
        settlementService.settleEvent(eventId, homeScore, awayScore);
        return ResponseEntity.ok("Event " + eventId + " settled. Funds distributed successfully.");
    }
}