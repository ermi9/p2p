package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fixture_provider_mapping")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FixtureProviderMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @Column(name="provider_name",nullable = false, length=50)
    private String providerName;

    @Column(name="external_fixture_id",nullable=false,length=100)
    private String externalFixtureId;
}