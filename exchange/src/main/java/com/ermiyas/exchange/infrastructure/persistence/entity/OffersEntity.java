package com.ermiyas.exchange.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OffersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outcome_id", nullable = false)
    private OutcomesEntity outcome;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal odds;

    @Column(name = "initial_stake", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialStake;

    @Column(name = "remaining_stake", nullable = false, precision = 19, scale = 4)
    private BigDecimal remainingStake;

    @Column(nullable = false, length = 50)
    private String status; // 'OPEN', 'CANCELLED', 'FILLED'

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
