package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "offers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maker_id")
    @JsonIncludeProperties({"id", "username"})
    @Setter(AccessLevel.NONE) 
    private User maker; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    @JsonIncludeProperties({"id", "homeTeam", "awayTeam", "startTime"})
    private Event event;

    @Enumerated(EnumType.STRING)
    @JsonProperty("predictedOutcome")
    private Outcome predictedOutcome;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "original_stake", nullable = false))
    private Money originalStake;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "remaining_stake", nullable = false))
    @Setter(AccessLevel.NONE) 
    private Money remainingStake;

    @Embedded
    private Odds odds;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Bet> bets = new ArrayList<>();

    /**
     * Refactored- Orchestrates the partial or full matching of an offer.
     * Calculates the Taker's liability based on the portion of the Maker's stake being matched.
     */
    public Bet fill(Money makerStakeToMatch, User taker, String reference) throws ExchangeException {
        // 1. Detailed validation before any state changes
        validateFillable(makerStakeToMatch);
        
        // 2. Calculate what the Taker must risk (Liability) to match this stake
        Money takerLiability = odds.calculateLiability(makerStakeToMatch);
        
        try {
            // 3. Deduct from the liquidity pool
            this.remainingStake = this.remainingStake.minus(makerStakeToMatch);
        } catch (IllegalBetException e) {
            // This would only trigger if validation failed or concurrent modification occurred
            throw new IllegalBetException("Integrity Error: Could not deduct " + makerStakeToMatch + " from available " + remainingStake);
        }
        
        // 4. Update the Offer's status (OPEN -> PARTIALLY_TAKEN -> TAKEN)
        this.updateStatusAfterFill();
        
        // 5. Build the resulting Matched Bet contract
        Bet bet = Bet.builder()
                .offer(this)
                .taker(taker)
                .makerStake(makerStakeToMatch)
                .takerLiability(takerLiability)
                .odds(this.odds)
                .reference(reference)
                .status(BetStatus.MATCHED)
                .build();
        
        this.bets.add(bet);
        return bet;
    }

    public void cancel() throws IllegalBetException {
        if (this.status == OfferStatus.TAKEN) {
            throw new IllegalBetException("Cancellation Failed: This offer has already been fully matched by other users.");
        }
        this.status = OfferStatus.CANCELLED;
    }

    /**
     * Refactored- Provides clear, actionable error messages for the UI.
     */
    private void validateFillable(Money amount) throws IllegalBetException {
        Objects.requireNonNull(amount, "System Error: Matched stake amount cannot be null.");
        
        if (this.status == OfferStatus.CANCELLED) {
            throw new IllegalBetException("Trade Failed: This offer was cancelled by the maker and is no longer active.");
        }
        
        if (this.status == OfferStatus.TAKEN) {
            throw new IllegalBetException("Trade Failed: This offer has already been fully matched by another user.");
        }

        if (amount.isGreaterThan(remainingStake)) {
            //  Descriptive message to solve the "inaccurate error" issue in the UI
            throw new IllegalBetException(String.format(
                "Trade Failed: Requested match of $%s exceeds the remaining available stake of $%s.",
                amount.value(), 
                remainingStake.value()
            ));
        }
    }

    private void updateStatusAfterFill() {
        this.status = this.remainingStake.isZero() ? OfferStatus.TAKEN : OfferStatus.PARTIALLY_TAKEN;
    }
}