package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaWalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByUserId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :id")
    Optional<Wallet> findByUserIdWithLock(Long id);
}