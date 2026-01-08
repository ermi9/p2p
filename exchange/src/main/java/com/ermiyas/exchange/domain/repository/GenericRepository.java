package com.ermiyas.exchange.domain.repository;

import java.util.List;
import java.util.Optional;

/**
   Parametric Polymorphism (Generics).
 * Centralizes common repository behavior to improve maintainability.
 */
public interface GenericRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
}