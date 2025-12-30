package com.ermiyas.exchange.domain.repository;

import com.ermiyas.exchange.domain.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findAllByOfferEventId(Long eventId);
}