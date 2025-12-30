package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.vo.Odds;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PURE OOP: Event Aggregate Root.
 * Represents a match/fixture. Stores reference odds for the UI
 * and handles the logic for determining the winning outcome.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID from external Sports API (e.g., The Odds API)
    @Column(unique = true)
    private String externalId;

    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    private Outcome result;

    // --- Reference Odds (Read-Only for Frontend) ---
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

    /**
     * Logic: Admin-triggered result update.
     * Maps scores to the Outcome enum.
     */
    public void determineWinner(int homeScore, int awayScore) {
        if (this.status == EventStatus.SETTLED) {
            throw new IllegalStateException("Event is already settled and cannot be modified");
        }

        if (homeScore > awayScore) {
            this.result = Outcome.HOME_WIN;
        } else if (awayScore > homeScore) {
            this.result = Outcome.AWAY_WIN;
        } else {
            this.result = Outcome.DRAW;
        }

        this.status = EventStatus.COMPLETED;
    }

    /**
     * Logic: Transitions the event to settled after funds are released.
     */
    public void markAsSettled() {
        if (this.status != EventStatus.COMPLETED) {
            throw new IllegalStateException("Cannot settle an event that is not marked COMPLETED");
        }
        this.status = EventStatus.SETTLED;
    }
}