package com.ermiyas.exchange.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bet_agreements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BetAgreementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private OffersEntity offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_user_id", nullable = false)
    private UsersEntity makerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taker_user_id", nullable = false)
    private UsersEntity takerUser;

    @Column(name = "maker_risk", nullable = false, precision = 19, scale = 4)
    private BigDecimal makerRisk;

    @Column(name = "taker_risk", nullable = false, precision = 19, scale = 4)
    private BigDecimal takerRisk;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal odds;

    @Column(nullable = false, length = 50)
    private String status; // 'ACTIVE', 'SETTLED', 'VOIDED'

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}