package com.ermiyas.exchange.api;

import com.ermiyas.exchange.application.OfferApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final OfferApplicationService offerService;

    /**
     * Endpoint to place a new betting offer.
     * POST /api/v1/exchange/offers
     */
    @PostMapping("/offers")
    public ResponseEntity<String> placeOffer(
            @RequestParam Long userId,
            @RequestParam Long outcomeId,
            @RequestParam BigDecimal stake,
            @RequestParam BigDecimal odds) {
        
        try {
            // This triggers the full domain logic
            offerService.createNewOffer(userId, outcomeId, stake, odds);
            return ResponseEntity.ok("Offer placed successfully and funds reserved.");
        } catch (Exception e) {
            // Captures InsufficientFunds or other domain violations
            return ResponseEntity.badRequest().body("Failed to place offer: " + e.getMessage());
        }
    }
}