package com.ermiyas.exchange.api;

import com.ermiyas.exchange.application.AdminSyncService;
import com.ermiyas.exchange.application.SettlementService;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.repository.user.AdminUserRepository; // Ensure this import exists
import com.ermiyas.exchange.domain.exception.ExchangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminSyncService adminSyncService;
    private final SettlementService settlementService;
    private final AdminUserRepository adminUserRepository; 

    private static final List<String> TOP_5_LEAGUES = List.of(
            "soccer_epl", "soccer_spain_la_liga", "soccer_germany_bundesliga",
            "soccer_italy_serie_a", "soccer_france_ligue_one"
    );


    private AdminUser getAdminContext() throws ExchangeException {
        // Fetch the first admin (created by your DataInitializer/UserFactory)
        return adminUserRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ExchangeException("Unauthorized: No Admin found in system."));
    }

    @PostMapping("/sync-fixtures")
    public ResponseEntity<String> syncFixtures() {
        try {
            AdminUser admin = getAdminContext();
            adminSyncService.syncLeagues(admin, TOP_5_LEAGUES); 
            return ResponseEntity.ok("Successfully synced fixtures.");
        } catch (ExchangeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-odds")
    public ResponseEntity<String> refreshOdds() {
        try {
            AdminUser admin = getAdminContext();
            adminSyncService.refreshOdds(admin, TOP_5_LEAGUES); 
            return ResponseEntity.ok("Reference odds updated.");
        } catch (ExchangeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-scores")
    public ResponseEntity<String> refreshScores() {
        try {
            AdminUser admin = getAdminContext();
            adminSyncService.refreshScores(admin, TOP_5_LEAGUES); 
            return ResponseEntity.ok("Latest scores fetched.");
        } catch (ExchangeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/settle/{eventId}")
    public ResponseEntity<String> settleMatch(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int homeScore,
            @RequestParam(defaultValue = "0") int awayScore) {
        
        try {
            AdminUser admin = getAdminContext();
            settlementService.settleEvent(admin, eventId, homeScore, awayScore);
            return ResponseEntity.ok("Event settled. Funds distributed.");
        } catch (ExchangeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}