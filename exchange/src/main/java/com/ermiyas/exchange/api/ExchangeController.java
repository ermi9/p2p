package com.ermiyas.exchange.api;

import com.ermiyas.exchange.application.TradeService;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.EventStatus;
import com.ermiyas.exchange.domain.repository.EventRepository;
import com.ermiyas.exchange.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final EventRepository eventRepository;
    private final TradeService tradeService;

    /**
     * Requirement: Users see fixtures and reference odds.
     */
    @GetMapping("/fixtures")
    public ResponseEntity<List<Event>> getActiveFixtures() {
        return ResponseEntity.ok(eventRepository.findAllByStatus(EventStatus.OPEN));
    }

    /**
     * THE MATCH ENDPOINT
     * This is what your curl -X POST .../match is hitting.
     */
    @PostMapping("/match")
    public ResponseEntity<String> matchOffer(
            @RequestParam Long offerId,
            @RequestParam Long takerId,
            @RequestParam String amount) {
        
        try {
            // We pass the amount as a string to Money.of() for safety
            tradeService.matchBet(offerId, takerId, Money.of(amount));
            return ResponseEntity.ok("Bet matched successfully!");
        } catch (Exception e) {
            // This will help you see the error in the response body if it fails
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}