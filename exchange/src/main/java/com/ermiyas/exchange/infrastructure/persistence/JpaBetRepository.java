package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.Bet;
import com.ermiyas.exchange.domain.repository.BetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaBetRepository extends JpaRepository<Bet, Long>, BetRepository {
    @Override
    List<Bet> findAllByOfferEventId(Long eventId);
}