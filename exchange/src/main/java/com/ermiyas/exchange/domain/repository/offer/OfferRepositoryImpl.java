package com.ermiyas.exchange.domain.repository.offer;

import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.infrastructure.persistence.JpaOfferRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OfferRepositoryImpl implements OfferRepository {
    private final JpaOfferRepository jpaOfferRepository;

    @Override public Offer save(Offer offer) { return jpaOfferRepository.save(offer); }
    @Override public Optional<Offer> findById(Long id) { return jpaOfferRepository.findById(id); }
    @Override public List<Offer> findAll() { return jpaOfferRepository.findAll(); }

    @Override
    public Optional<Offer> findByIdWithLock(Long id) {
        return jpaOfferRepository.findWithLockById(id);
    }
    @Override
    public List<Offer> findAllByEventId(Long eventId){
        return jpaOfferRepository.findByEventId(eventId);
    }
    @Override
    public long count(){
        return jpaOfferRepository.count();
    }
}