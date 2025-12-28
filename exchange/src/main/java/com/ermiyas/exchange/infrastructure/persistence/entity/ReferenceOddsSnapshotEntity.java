package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reference_odds_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceOddsSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    
    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "outcome_name", nullable = false)
    private String outcomeName; // e.g., "HOME", "AWAY", "DRAW"

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}