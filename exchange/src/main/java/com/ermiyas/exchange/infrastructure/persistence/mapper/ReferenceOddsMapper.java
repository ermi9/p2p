package com.ermiyas.exchange.infrastructure.persistence.mapper;

import com.ermiyas.exchange.infrastructure.persistence.entity.ReferenceOddsSnapshotEntity;
import com.ermiyas.exchange.infrastructure.persistence.entity.EventEntity;
import com.ermiyas.exchange.infrastructure.persistence.jpa.EventJpaRepository;
import com.ermiyas.exchange.referenceOdds.domain.OddsSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReferenceOddsMapper {

    private final EventJpaRepository eventJpaRepository;

    /**
     * Converts Domain Entity to JPA Persistence Entity.
     */
    public ReferenceOddsSnapshotEntity toEntity(OddsSnapshot domain) {
        if (domain == null) return null;

        // Use standard Getters because OddsSnapshot is now a Class
        EventEntity eventProxy = eventJpaRepository.getReferenceById(domain.getEventId());

        return ReferenceOddsSnapshotEntity.builder()
                .event(eventProxy)
                .providerName(domain.getProviderName())
                .outcomeName(domain.getOutcomeName())
                .price(domain.getPrice())
                .fetchedAt(domain.getFetchedAt())
                .build();
    }

    /**
     * Converts JPA Persistence Entity back to Domain Entity.
     */
    public OddsSnapshot toDomain(ReferenceOddsSnapshotEntity entity) {
        if (entity == null) return null;

        // Use the Builder to avoid "No suitable constructor" errors
        return OddsSnapshot.builder()
                .eventId(entity.getEvent().getId())
                .providerName(entity.getProviderName())
                .outcomeName(entity.getOutcomeName())
                .price(entity.getPrice())
                .fetchedAt(entity.getFetchedAt())
                .build();
    }
}