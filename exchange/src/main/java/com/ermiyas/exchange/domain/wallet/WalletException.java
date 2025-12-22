package com.ermiyas.exchange.domain.wallet;

public abstract class WalletException extends RuntimeException{
protected WalletException(String message){
    super(message);
}    
}
