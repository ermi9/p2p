package com.ermiyas.exchange.domain.model.user;
import com.ermiyas.exchange.domain.model.Wallet;

//demonstrates multityping

public interface WalletOwner {
    Wallet getWallet();
    void setWallet(Wallet wallet);

    
}
