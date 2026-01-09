package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos.AdminSettleRequest;
import com.ermiyas.exchange.application.service.AdminSyncService;
import com.ermiyas.exchange.application.service.AdminSettlementService;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminSyncService syncService;
    private final AdminSettlementService settlementService;
    private final UserRepository userRepository;

    @PostMapping("/sync")
    public ResponseEntity<String> syncAll() {
        syncService.syncAllFixtures(); // Refactored to handle all leagues
        return ResponseEntity.ok("Sync triggered successfully.");
    }

    @PostMapping("/settle")
    public ResponseEntity<String> settleMarkets(
            @RequestBody AdminSettleRequest request, 
            @RequestHeader("X-Admin-Id") Long adminId) throws ExchangeException {
        
        AdminUser admin = (AdminUser) userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("Admin not found."));

        settlementService.settleMarketResults(admin, request.getExternalIds()); //
        return ResponseEntity.ok("Settlement processed.");
    }
}