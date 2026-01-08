package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PURE OOP: Event Aggregate Root.
 * Updated: Added 'league' field so the AdminSettlementService can 
 * fetch the correct scores from the external API.
 */
@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String externalId;

    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;

    /**
     *  Added League field.
     */
    @Enumerated(EnumType.STRING)
    private League league;

    @Enumerated(EnumType.STRING)
    private MarketType marketType;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private Outcome result;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ref_home_odds"))
    private Odds refHomeOdds;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ref_away_odds"))
    private Odds refAwayOdds;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ref_draw_odds"))
    private Odds refDrawOdds;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Offer> offers = new ArrayList<>();

    public void processResult(int homeScore, int awayScore, SettlementStrategy strategy) throws ExchangeException {
        validateSettlementState();
        this.result = strategy.determineWinner(homeScore, awayScore);
        this.status = EventStatus.COMPLETED;
    }

    public void markAsSettled() throws ExchangeException {
        if (this.status != EventStatus.COMPLETED) {
            throw new IllegalBetException("State Violation: Cannot settle an event that is not COMPLETED.");
        }
        
        for (int i = 0; i < this.offers.size(); i++) {
            Offer offer = this.offers.get(i);
            OfferStatus offerStatus = offer.getStatus();
            
            if (offerStatus == OfferStatus.OPEN || offerStatus == OfferStatus.PARTIALLY_TAKEN) {
                throw new IllegalBetException("Integrity Error: Offer #" + offer.getId() + " is still active. Finalize or Cancel it first.");
            }
        }

        this.status = EventStatus.SETTLED;
    }

    private void validateSettlementState() throws IllegalBetException {
        if (this.status == EventStatus.SETTLED) {
            throw new IllegalBetException("Integrity Error: Event is already settled and immutable.");
        }
    }
}