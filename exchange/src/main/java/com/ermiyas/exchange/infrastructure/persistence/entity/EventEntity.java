package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="external_id",unique = true)
    private String externalId; // our main fixture provider ID

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="start_time",nullable=false)
    private LocalDateTime startTime;

    @Column(name="league_code",nullable = false)
    private String leagueCode;

    @Column(name="status",nullable=false)
    private String status;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at",nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }



}