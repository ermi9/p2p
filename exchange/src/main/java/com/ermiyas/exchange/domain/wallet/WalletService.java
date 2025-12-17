package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.application.ports.WalletRepository; import com.ermiyas.exchange.common.Money; import java.util.Objects;

public final class WalletService { private final WalletRepository walletRepository;

public WalletService(WalletRepository walletRepository) {
    this.walletRepository = Objects.requireNonNull(walletRepository);
}

public void reserve(long userId, Money amount) {
    Wallet wallet = walletRepository.findByUserId(userId);
    wallet.reserve(amount);
    walletRepository.save(wallet);
}

public void release(long userId, Money amount) {
    Wallet wallet = walletRepository.findByUserId(userId);
    wallet.release(amount);
    walletRepository.save(wallet);
}

public Wallet findByUserId(long userId) {
    return walletRepository.findByUserId(userId);
}

public void credit(long userId, Money amount, String reference) {
    Wallet wallet = walletRepository.findByUserId(userId);
    wallet.deposit(amount, reference);
    walletRepository.save(wallet);
}

public void withdraw(long userId, Money amount, String reference) {
    Wallet wallet = walletRepository.findByUserId(userId);
    wallet.withdraw(amount, reference);
    walletRepository.save(wallet);
}

public void deposit(long userId, Money amount, String reference) {
    Wallet wallet = walletRepository.findByUserId(userId);
    wallet.deposit(amount, reference);
    walletRepository.save(wallet);
}

public Money availableBalance(long userId) {
    return walletRepository.findByUserId(userId).availableBalance();
}

public Money totalBalance(long userId) {
    return walletRepository.findByUserId(userId).totalBalance();
}
}