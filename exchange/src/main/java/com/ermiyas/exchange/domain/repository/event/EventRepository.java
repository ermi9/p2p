package com.ermiyas.exchange.domain.repository.event;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.EventStatus;
import com.ermiyas.exchange.domain.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends GenericRepository<Event, Long> {
    Optional<Event> getByExternalId(String externalId);
    List<Event> findAllByStatus(EventStatus status);
}