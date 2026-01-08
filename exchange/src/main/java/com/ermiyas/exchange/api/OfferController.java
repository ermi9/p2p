package com.ermiyas.exchange.api;

import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.infrastructure.persistence.JpaUserRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @Transactional
    public ResponseEntity<?> createOffer(
            @RequestParam Long eventId,
            @RequestParam Long makerId,
            @RequestParam String stake,
            @RequestParam String outcome,
            @RequestParam String makerOdds) {

        try {
            Event event = eventRepository.getById(eventId);
            if (event == null) {
                throw new ExchangeException("Event not found with ID: " + eventId);
            }
            
            User maker = userRepository.findById(makerId)
                    .orElseThrow(() -> new ExchangeException("User not found with ID: " + makerId));
            
            Wallet wallet = walletRepository.getByUserId(makerId);
            if (wallet == null) {
                throw new ExchangeException("Wallet not found for user: " + makerId);
            }

            Odds customOdds = Odds.of(new java.math.BigDecimal(makerOdds));
            Money amountToStake = Money.of(stake);

            wallet.reserve(amountToStake);
            walletRepository.save(wallet);

            Offer offer = Offer.builder()
                    .event(event)
                    .maker(maker)
                    .odds(customOdds)
                    .originalStake(amountToStake)
                    .remainingStake(amountToStake)
                    .predictedOutcome(Outcome.valueOf(outcome))
                    .status(OfferStatus.OPEN)
                    .build();

            Offer savedOffer = offerRepository.save(offer);
            return ResponseEntity.ok("Offer created with ID: " + savedOffer.getId());

        } catch (ExchangeException e) {
            // Robustness: Catching checked domain exceptions for specific HTTP feedback
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid outcome specified.");
        }
    }
}