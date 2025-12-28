package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name="wallets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WalletsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable=false,unique = true)
    private UsersEntity userId;

    @Column(name="total_balance",nullable = false,precision = 19, scale=4)
    private BigDecimal totalBalance;

    @Column(name="reserved_balance",nullable = false,precision = 19,scale=4)
    private BigDecimal reservedBalance;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
        if(this.totalBalance==null) this.totalBalance=BigDecimal.ZERO;
        if(this.reservedBalance==null) this.reservedBalance=BigDecimal.ZERO;
    }
    @PreUpdate
    protected void onUpdate(){
        this.updatedAt=LocalDateTime.now();
    }

    

    
}
