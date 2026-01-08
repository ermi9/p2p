package com.ermiyas.exchange.domain.repository.bet;

import com.ermiyas.exchange.domain.model.Bet;
import com.ermiyas.exchange.infrastructure.persistence.JpaBetRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BetRepositoryImpl implements BetRepository {
    private final JpaBetRepository jpaBetRepository;

    @Override public Bet save(Bet bet) { return jpaBetRepository.save(bet); }
    @Override public Optional<Bet> findById(Long id) { return jpaBetRepository.findById(id); }
    @Override public List<Bet> findAll() { return jpaBetRepository.findAll(); }

    @Override
    public List<Bet> findAllByOfferEventId(Long eventId) {

        return jpaBetRepository.findAllByOfferEventId(eventId);
    }
}