package com.ermiyas.exchange.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outcomes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OutcomesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the Market (e.g., Match Winner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    private MarketsEntity market;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
