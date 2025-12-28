package com.ermiyas.exchange.infrastructure.persistence;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.wallet.Wallet;
import com.ermiyas.exchange.infrastructure.persistence.entity.WalletsEntity;
import com.ermiyas.exchange.infrastructure.persistence.jpa.WalletsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletPersistenceAdapter {

    private final WalletsJpaRepository walletRepository;

    public Wallet loadWalletByUserId(Long userId) {
        // Look up the wallet entity using the user_id relationship
        WalletsEntity entity = walletRepository.findByUserId_Id(userId);
        
        if (entity == null) {
            throw new RuntimeException("Wallet not found for User ID: " + userId);
        }

        // 1. Create the domain object using Total Balance
        Wallet wallet = new Wallet(new Money(entity.getTotalBalance()));

        // 2. Set the internal Reserved state
        if (entity.getReservedBalance().signum() > 0) {
            wallet.reserve(new Money(entity.getReservedBalance()));
        }

        return wallet;
    }

    /**
     * Maps the updated Domain state back to the Database Entity.
     */
    public void saveWallet(Long userId, Wallet wallet) {
        WalletsEntity entity = walletRepository.findByUserId_Id(userId);

        if (entity != null) {
            // Available is calculated in domain, but we save Total and Reserved to DB
            entity.setTotalBalance(wallet.totalBalance().value());
            entity.setReservedBalance(wallet.reservedBalance().value());
            
            walletRepository.save(entity);
        }
    }
}