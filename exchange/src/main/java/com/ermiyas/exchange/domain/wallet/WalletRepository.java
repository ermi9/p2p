package com.ermiyas.exchange.domain.wallet;


public interface WalletRepository {
    Wallet findByUserId(Long userId);
    void save(Wallet wallet);
}