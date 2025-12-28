package com.ermiyas.exchange.referenceOdds.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "odds_snapshots")
@Getter @Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class OddsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "outcome_name")
    private String outcomeName;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;
}