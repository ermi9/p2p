package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos.CreateOfferRequest;
import com.ermiyas.exchange.api.dto.ExchangeDtos.MatchBetRequest;
import com.ermiyas.exchange.application.service.CommissionService;
import com.ermiyas.exchange.application.service.OfferService;
import com.ermiyas.exchange.application.service.TradeService;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.vo.StandardPercentagePolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final OfferService offerService;
    private final TradeService tradeService;
    private final CommissionService commissionService; 

    /**
     * MAKER ACTION: Create a new offer.
     */
    @PostMapping("/offers")
        public ResponseEntity<Long> createOffer(@RequestBody CreateOfferRequest request) throws ExchangeException {
            Long id = offerService.createOffer(
                request.getEventId(), 
                request.getMakerId(), 
                request.getOutcome(), 
                Odds.of(request.getOdds()), 
                Money.of(request.getStake()) 
            );
            return ResponseEntity.ok(id);
        }

    /**
     * TAKER ACTION: Match an existing offer.
     */
    @PostMapping("/trades/match")
    public ResponseEntity<String> matchTrade(@RequestBody MatchBetRequest request) throws ExchangeException {
        // Use request.getTakerId() instead of the @RequestHeader
        tradeService.matchBet(
            request.getOfferId(), 
            request.getTakerId(), 
            Money.of(request.getAmountToMatch())
        );
        return ResponseEntity.ok("Trade matched successfully.");
    }
    @DeleteMapping("/offers/{offerId}")
    public ResponseEntity<Void> cancelOffer(
            @PathVariable Long offerId, 
            @RequestHeader("X-User-Id") Long userId) throws ExchangeException {
        
        offerService.cancelOffer(offerId, userId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/commission-preview")
    public ResponseEntity<Map<String, BigDecimal>> getPreview(
            @RequestParam String profit, 
            @RequestParam BigDecimal rate) throws ExchangeException {
        
        Money grossProfit = Money.of(profit);
        CommissionPolicy policy = new StandardPercentagePolicy(rate);
        
        return ResponseEntity.ok(Map.of(
            "netProfit", commissionService.estimateNetProfit(grossProfit, policy).value(),
            "commissionCharge", commissionService.estimateCommission(grossProfit, policy).value()
        ));
    }

}