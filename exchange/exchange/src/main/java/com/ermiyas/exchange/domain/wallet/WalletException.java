package com.ermiyas.exchange.domain.wallet;

/**
 * Base domain exception for wallet issues, added for clarity in services.
 */
public class WalletException extends RuntimeException {
    public WalletException(String message) {
        super(message);
    }
}
