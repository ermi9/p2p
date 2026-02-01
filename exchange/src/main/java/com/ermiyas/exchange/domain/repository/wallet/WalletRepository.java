package com.ermiyas.exchange.domain.repository.wallet;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.repository.GenericRepository;
import java.util.Optional;

/**
 * Wallet Repository Interface.
 */
public interface WalletRepository extends GenericRepository<Wallet, Long> {
    
    Optional<Wallet> getByUserId(Long userId);

    // 
    Optional<Wallet> getByUserIdWithLock(Long userId);
}