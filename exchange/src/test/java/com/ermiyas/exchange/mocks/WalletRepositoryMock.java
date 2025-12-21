package com.ermiyas.exchange.mocks;

import com.ermiyas.exchange.application.ports.WalletRepository;
import com.ermiyas.exchange.domain.wallet.Wallet;
import java.util.HashMap;
import java.util.Map;

public class WalletRepositoryMock implements WalletRepository {
    private final Map<Long, Wallet> store = new HashMap<>();

    @Override
    public void save(Wallet wallet) {
        store.put(wallet.userId(), wallet);
    }

    @Override
    public Wallet findByUserId(long userId) {
        return store.get(userId);
    }
}