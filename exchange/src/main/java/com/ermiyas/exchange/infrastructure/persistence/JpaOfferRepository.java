package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.domain.repository.OfferRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaOfferRepository extends JpaRepository<Offer, Long>, OfferRepository {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offer o WHERE o.id = :id")
    Optional<Offer> findByIdWithLock(Long id);
}