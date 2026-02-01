package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Aggregate Root.
 * Updated to include reference source names for professional odd tracking.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
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

    // Existing Odds Value Objects
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ref_home_odds"))
    private Odds refHomeOdds;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ref_away_odds"))
    private Odds refAwayOdds;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ref_draw_odds"))
    private Odds refDrawOdds;

    // These store the name of the bookmaker that provided the best odds above.
    private String refHomeSource;
    private String refAwaySource;
    private String refDrawSource;
    // 

    private Integer finalHomeScore;//to settle it later
    private Integer finalAwayScore;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnore
    private List<Offer> offers = new ArrayList<>();

    public void processResult(int homeScore, int awayScore, SettlementStrategy strategy) throws ExchangeException {
        validateSettlementState();
        
        // Save the scores so the Admin can see them in the UI
        this.finalHomeScore = homeScore;
        this.finalAwayScore = awayScore;
        
        // Determine winner based on the provided strategy
        this.result = strategy.determineWinner(homeScore, awayScore);
        
        //  Flip status to COMPLETED to flag this for the Admin
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