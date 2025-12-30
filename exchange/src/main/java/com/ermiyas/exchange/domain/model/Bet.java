package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * PURE OOP: Bet Entity.
 * Represents a legally binding match between a Maker and a Taker.
 * Demonstrates Composition: A Bet is composed of an Offer, Users, and Value Objects.
 */
@Entity
@Table(name = "bets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer; // The parent offer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taker_id")
    private User taker; // The user who provided liquidity (Layer)

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "maker_stake", nullable = false))
    private Money makerStake; // The slice of the Maker's stake covered by this bet

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "taker_liability", nullable = false))
    private Money takerLiability; // The total risk the Taker has accepted

    @Embedded
    private Odds odds; // Snapshot of odds at the moment of matching

    private String reference; // External audit/tracking reference
    
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private BetStatus status; // MATCHED, SETTLED

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = BetStatus.MATCHED;
        }
    }

    /**
     * Logic: Returns the Maker of the parent offer.
     */
    public User getMaker() {
        return offer.getMaker();
    }
}