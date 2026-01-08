package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deposit(Long userId, Money amount) throws ExchangeException {
        Wallet wallet = walletRepository.getByUserIdWithLock(userId)
                .orElseThrow(() -> new UserNotFoundException("Deposit Failed: Wallet not found for user ID: " + userId));

        wallet.deposit(amount);
        walletRepository.save(wallet);
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long userId, Money amount) throws ExchangeException {
        Wallet wallet = walletRepository.getByUserIdWithLock(userId)
                .orElseThrow(() -> new UserNotFoundException("Withdrawal Failed: Wallet not found for user ID: " + userId));

        wallet.withdraw(amount); 
        walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public Wallet getUserWallet(Long userId) throws ExchangeException {
        return walletRepository.getByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Lookup Failed: Wallet not found for user ID: " + userId));
    }
}