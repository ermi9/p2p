package com.ermiyas.exchange.domain.repository;

import com.ermiyas.exchange.domain.model.Offer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    /**
     * Requirement: Match Settlement & Cleanup.
     * Finds all offers for an event so that unmatched stakes can be returned 
     * once the match result is known.
     */
    List<Offer> findAllByEventId(Long eventId);

    /**
     * Requirement 1: Pessimistic Locking.
     * Prevents multiple takers from matching the same stake simultaneously.
     * This is used by the TradeService during the matching process.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offer o WHERE o.id = :id")
    Optional<Offer> findByIdWithLock(@Param("id") Long id);
}