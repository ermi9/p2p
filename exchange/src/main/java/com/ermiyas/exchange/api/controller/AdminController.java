package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos.AdminSettleRequest;
import com.ermiyas.exchange.api.dto.ExchangeDtos.DashboardStatsResponse;
import com.ermiyas.exchange.application.service.AdminSyncService;
import com.ermiyas.exchange.application.service.AdminSettlementService;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5501")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminSyncService syncService;
    private final AdminSettlementService settlementService;
    private final UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(DashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .activeFixtures(syncService.getActiveFixtureCount())
                .lockedStake(settlementService.calculateTotalLockedStake())
                .build());
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncAll() {
        // Manual trigger still works and marks matches as COMPLETED
        syncService.syncAllFixtures();
        return ResponseEntity.ok("External synchronization triggered successfully.");
    }

    @PostMapping("/settle")
    public ResponseEntity<String> settleMarkets(
            @RequestBody AdminSettleRequest request, 
            @RequestHeader("X-Admin-Id") Long adminId) throws ExchangeException {
        
        AdminUser admin = (AdminUser) userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("Admin not found."));

        settlementService.settleMarketResults(admin, request.getExternalIds());
        return ResponseEntity.ok("Market settlement processed.");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}