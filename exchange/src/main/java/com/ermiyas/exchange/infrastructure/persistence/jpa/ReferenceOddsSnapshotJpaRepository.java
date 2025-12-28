package com.ermiyas.exchange.infrastructure.persistence.jpa;

import com.ermiyas.exchange.infrastructure.persistence.entity.ReferenceOddsSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceOddsSnapshotJpaRepository extends JpaRepository<ReferenceOddsSnapshotEntity, Long> {
}
