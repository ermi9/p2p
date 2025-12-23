package com.ermiyas.exchange.infrastructure.persistence.inmemory;

import com.ermiyas.exchange.application.ports.WalletRepository;
import com.ermiyas.exchange.domain.wallet.Wallet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple concurrent map storage so wallets can be shared across use cases.
 */
public final class InMemoryWalletRepository implements WalletRepository {

    private final Map<Long, Wallet> walletsByUserId = new ConcurrentHashMap<>();

    @Override
    public Wallet findByUserId(long userId) {
        Wallet wallet = walletsByUserId.get(userId);
        if (wallet == null) {
            throw new IllegalStateException("Wallet not found for user " + userId);
        }
        return wallet;
    }

    @Override
    public void save(Wallet wallet) {
        walletsByUserId.put(wallet.userId(), wallet);
    }
}
