package com.ermiyas.exchange.application.ports;
import com.ermiyas.exchange.domain.wallet.Wallet;
public interface WalletRepository {
    Wallet findByUserId(long userId);
    void save(Wallet wallet);
}
