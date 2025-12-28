package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name="markets")
@Getter@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarketsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="event_id", nullable =false)
    private EventEntity event;

    @Column(name="created_at",nullable=false,updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }
    
}
