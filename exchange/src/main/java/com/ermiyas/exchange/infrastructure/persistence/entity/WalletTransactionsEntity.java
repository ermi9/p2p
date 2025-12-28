package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name="wallet_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class WalletTransactionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="wallet_id",nullable = false)
    private WalletsEntity walletId;

    @Column(name="amount",nullable = false)
    private BigDecimal amount;

    @Column(name="transaction_type",nullable=false,length=50)
    private String transactionType;

    @Column(name="ref_type",length=50)
    private String refType;

    @Column(name="ref_id")
    private Integer refId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }


    
}
