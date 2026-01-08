package com.ermiyas.exchange.domain.repository.wallet;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.repository.GenericRepository;
import java.util.Optional;

/**
 * PURE OOP: Wallet Repository Interface.
 * We include the WithLock method here so the domain knows we can 
 * perform thread-safe financial operations.
 */
public interface WalletRepository extends GenericRepository<Wallet, Long> {
    
    Optional<Wallet> getByUserId(Long userId);

    // Added this to fix the TradeService compilation error
    Optional<Wallet> getByUserIdWithLock(Long userId);
}