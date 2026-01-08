package com.ermiyas.exchange.domain.repository.user;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.GenericRepository;
import java.util.Optional;

/**
  Inherits common CRUD and specializes with domain-specific lookups.
 */
public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByUsername(String username);
}