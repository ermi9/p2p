package com.ermiyas.exchange.infrastructure.persistence.jpa;

import com.ermiyas.exchange.infrastructure.persistence.entity.OutcomesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutcomeJpaRepository extends JpaRepository<OutcomesEntity, Long> {
    List<OutcomesEntity> findByMarketId(Long marketId);
}