package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
}