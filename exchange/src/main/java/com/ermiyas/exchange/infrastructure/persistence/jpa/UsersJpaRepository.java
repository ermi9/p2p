package com.ermiyas.exchange.infrastructure.persistence.jpa;

import com.ermiyas.exchange.infrastructure.persistence.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsersJpaRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByEmail(String email);
}