package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.application.ports.WalletRepository;
import com.ermiyas.exchange.common.Money;

/**
 * Thin service to wrap repository lookups and persist wallet changes.
 */
public final class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet findByUserId(long userId) {
        return walletRepository.findByUserId(userId);
    }

    public void deposit(long userId, Money amount, String reference) {
        Wallet wallet = walletRepository.findByUserId(userId);
        wallet.deposit(amount, reference);
        walletRepository.save(wallet);
    }

    public void withdraw(long userId, Money amount, String reference) {
        Wallet wallet = walletRepository.findByUserId(userId);
        wallet.withdraw(amount, reference);
        walletRepository.save(wallet);
    }

    public void debitForBet(long userId, Money amount, String reference) {
        Wallet wallet = walletRepository.findByUserId(userId);
        wallet.debitForBet(amount, reference);
        walletRepository.save(wallet);
    }

    public void creditForBet(long userId, Money amount, String reference) {
        Wallet wallet = walletRepository.findByUserId(userId);
        wallet.creditForBet(amount, reference);
        walletRepository.save(wallet);
    }
}
