package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.Offer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
@Repository
public interface JpaOfferRepository extends JpaRepository<Offer, Long> {
    
    List<Offer> findByEventId(Long eventId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offer o WHERE o.id = :id")
    Optional<Offer> findWithLockById(Long id);
}