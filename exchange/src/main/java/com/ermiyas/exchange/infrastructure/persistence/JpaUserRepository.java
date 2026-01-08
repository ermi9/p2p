package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}