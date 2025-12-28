package com.ermiyas.exchange.infrastructure.persistence.jpa;

import com.ermiyas.exchange.infrastructure.persistence.entity.BetAgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BetAgreementJpaRepository extends JpaRepository<BetAgreementEntity, Long> {
}