package com.ermiyas.exchange.referenceOdds.domain;

import com.ermiyas.exchange.infrastructure.persistence.jpa.FixtureProviderMappingJpaRepository;
import com.ermiyas.exchange.infrastructure.persistence.entity.FixtureProviderMappingEntity;
import com.ermiyas.exchange.referenceOdds.infrastructure.external.TheOddsApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceOddsService {

    private final ReferenceOddsRepository repository;
    private final FixtureProviderMappingJpaRepository mappingRepository;

    @Transactional
public void ingestAndLinkOdds(TheOddsApiDto externalMatch) {
    FixtureProviderMappingEntity mapping = mappingRepository.findByExternalFixtureId(externalMatch.id());

    if (mapping != null) {
        Long internalEventId = mapping.getEvent().getId();
        List<OddsSnapshot> snapshots = externalMatch.toSnapshots(internalEventId);

        // LOGGING FOR DEBUGGING
        log.info("FOUND MAPPING! External ID: {} maps to Internal Event: {}", externalMatch.id(), internalEventId);
        log.info("SNAPSHOTS GENERATED: {}", snapshots.size());

        if (!snapshots.isEmpty()) {
            repository.saveAll(snapshots);
            log.info("DB SAVE CALLED for {} rows", snapshots.size());
        }
    } else {
        log.warn("CRAWLER: No mapping found for external ID: {}", externalMatch.id());
    }
}
}