package com.ermiyas.exchange.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private BetAgreementEntity agreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_user_id", nullable = false)
    private UsersEntity winner;

    @Column(name = "payout_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal payoutAmount;

    @Column(name = "commission_paid", nullable = false, precision = 19, scale = 4)
    private BigDecimal commissionPaid;

    @Column(name = "settled_at", updatable = false)
    private LocalDateTime settledAt;

    @PrePersist
    protected void onCreate() {
        this.settledAt = LocalDateTime.now();
    }
}
