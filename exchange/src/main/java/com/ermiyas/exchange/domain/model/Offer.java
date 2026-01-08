package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Offer Entity.
 * Demonstrates Strict Encapsulation, State Management, and Robust Exception Handling.
 */
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Event event;

    @Enumerated(EnumType.STRING)
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
    private List<Bet> bets = new ArrayList<>();



    public Bet fill(Money makerStakeToMatch, User taker, String reference) throws ExchangeException {
        validateFillable(makerStakeToMatch);

        Money takerLiability = odds.calculateLiability(makerStakeToMatch);

        try {
            this.remainingStake = this.remainingStake.minus(makerStakeToMatch);
        } catch (IllegalBetException e) {
            throw new IllegalBetException("Something went wrong");        }
        
        this.updateStatusAfterFill();

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

    /**
     * Cancels the offer if it hasn't been fully taken.
     * Uses custom IllegalBetException instead of generic IllegalStateException.
     */
    public void cancel() throws IllegalBetException {
        if (this.status == OfferStatus.TAKEN) {
            throw new IllegalBetException("Cannot cancel an offer that is already fully taken.");
        }
        this.status = OfferStatus.CANCELLED;
    }


    private void validateFillable(Money amount) throws IllegalBetException {
        Objects.requireNonNull(amount, "Matched stake cannot be null");
        
        if (this.status == OfferStatus.CANCELLED) {
            throw new IllegalBetException("Cannot fill a cancelled offer.");
        }

        if (amount.isGreaterThan(remainingStake)) {
            throw new IllegalBetException("Requested match amount " + amount + " exceeds available stake " + remainingStake);
        }
    }

    private void updateStatusAfterFill() {
        if (this.remainingStake.isZero()) {
            this.status = OfferStatus.TAKEN;
        } else {
            this.status = OfferStatus.PARTIALLY_TAKEN;
        }
    }
}