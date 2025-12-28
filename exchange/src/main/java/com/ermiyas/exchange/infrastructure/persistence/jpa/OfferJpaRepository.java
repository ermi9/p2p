package com.ermiyas.exchange.infrastructure.persistence.jpa;

import com.ermiyas.exchange.infrastructure.persistence.entity.OffersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfferJpaRepository extends JpaRepository<OffersEntity, Long> {
    List<OffersEntity> findByStatus(String status);
}