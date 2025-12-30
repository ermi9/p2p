package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PURE OOP: Offer Aggregate Root.
 * Represents the Maker's "Back" bet.
 * Supports partial matching by multiple Takers.
 */
@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maker_id")
    @JsonIncludeProperties({"id", "username"})
    private User maker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Event event;

    @Enumerated(EnumType.STRING)
    private Outcome predictedOutcome; // The outcome the Maker is betting WILL happen

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "original_stake", nullable = false))
    private Money originalStake;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "remaining_stake", nullable = false))
    private Money remainingStake;

    @Embedded
    private Odds odds;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bet> bets = new ArrayList<>();

    /**
     * PURE OOP Logic: Handles a Taker matching a portion of this offer.
     * Calculates Taker Liability = matchedMakerStake * (odds - 1).
     */
    public Bet fill(Money makerStakeToMatch, User taker, String reference) {
        Objects.requireNonNull(makerStakeToMatch, "Matched stake cannot be null");
        
        if (makerStakeToMatch.compareTo(remainingStake) > 0) {
            throw new IllegalStateException("Requested match exceeds available stake in offer");
        }

        // Taker's risk = Maker's stake * (Odds - 1)
        Money takerLiability = Money.of(makerStakeToMatch.value().multiply(odds.profitMultiplier()));

        // Update internal state
        this.remainingStake = this.remainingStake.minus(makerStakeToMatch);
        this.updateStatusAfterFill();

        // Create the matched Bet contract
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

    private void updateStatusAfterFill() {
        if (this.remainingStake.value().signum() == 0) {
            this.status = OfferStatus.TAKEN;
        } else {
            this.status = OfferStatus.PARTIALLY_TAKEN;
        }
    }
}