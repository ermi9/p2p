package com.ermiyas.exchange.domain.wallet;

public interface WalletRepository {
Wallet findByUserId(long userId);
void save(Wallet wallet);    
}
