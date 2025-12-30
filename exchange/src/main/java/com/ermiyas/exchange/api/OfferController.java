package com.ermiyas.exchange.api;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.repository.*;
import com.ermiyas.exchange.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferRepository offerRepository;
    private final EventRepository eventRepository;
    private final JpaUserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
     * Requirement: Maker creates an offer.
     * This locks the maker's stake in their wallet.
     */
   @PostMapping
@Transactional
public ResponseEntity<?> createOffer(
        @RequestParam Long eventId,
        @RequestParam Long makerId,
        @RequestParam String stake,
        @RequestParam String outcome,
        @RequestParam String makerOdds) { // Added makerOdds parameter

    // 1. Fetch dependencies
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
    User maker = userRepository.findById(makerId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    Wallet wallet = walletRepository.findByUserId(makerId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
    
    // 2. Process the Custom Odds and Stake
    Odds customOdds = Odds.of(new java.math.BigDecimal(makerOdds));
    Money amountToStake = Money.of(stake);

    // 3. Financial Logic: Reserve the maker's stake
    wallet.reserve(amountToStake);
    walletRepository.save(wallet);

    // 4. Create Offer with the User's chosen odds
    Offer offer = Offer.builder()
            .event(event)
            .maker(maker)
            .odds(customOdds) // Using the Maker's input!
            .originalStake(amountToStake)
            .remainingStake(amountToStake)
            .predictedOutcome(Outcome.valueOf(outcome))
            .status(OfferStatus.OPEN)
            .build();

    Offer savedOffer = offerRepository.save(offer);
    return ResponseEntity.ok("Offer created with ID: " + savedOffer.getId() + " at custom odds: " + customOdds.value());
}
}