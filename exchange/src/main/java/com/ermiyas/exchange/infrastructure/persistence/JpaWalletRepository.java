package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.repository.WalletRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaWalletRepository extends JpaRepository<Wallet, Long>, WalletRepository {
    @Override
    Optional<Wallet> findByUserId(Long userId);
}