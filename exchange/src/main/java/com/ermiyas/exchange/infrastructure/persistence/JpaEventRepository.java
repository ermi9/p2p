package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.EventStatus;
import com.ermiyas.exchange.domain.repository.EventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaEventRepository extends JpaRepository<Event, Long>, EventRepository {
    // Spring Data JPA automatically implements findByExternalId and findAllByStatus
    @Override
    Optional<Event> findByExternalId(String externalId);

    @Override
    List<Event> findAllByStatus(EventStatus status);
}