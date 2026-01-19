package com.ermiyas.exchange.domain.repository.event;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.EventStatus;
import com.ermiyas.exchange.infrastructure.persistence.JpaEventRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {
    private final JpaEventRepository jpaEventRepository;

    @Override public Event save(Event event) { return jpaEventRepository.save(event); }
    @Override public Optional<Event> findById(Long id) { return jpaEventRepository.findById(id); }
    @Override public List<Event> findAll() { return jpaEventRepository.findAll(); }

    @Override
    public Optional<Event> getByExternalId(String externalId) {
        return jpaEventRepository.findByExternalId(externalId);
    }

    @Override
    public List<Event> findAllByStatus(EventStatus status) {
        return jpaEventRepository.findAllByStatus(status);
    }
    @Override
    public long count(){
        return jpaEventRepository.count();
    }
     @Override
    public void deleteById(Long id){
         jpaEventRepository.deleteById(id);
    }
}