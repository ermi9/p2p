package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.common.Money;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable record describing a wallet balance movement.
 */
public final class WalletTransaction {

    private final WalletTransactionType type;
    private final Money amount;
    private final String reference;
    private final Instant createdAt;

    private WalletTransaction(WalletTransactionType type, Money amount, String reference, Instant createdAt) {
        this.type = type;
        this.amount = amount;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public static WalletTransaction of(WalletTransactionType type, Money amount, String reference) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(reference, "reference");
        return new WalletTransaction(type, amount, reference, Instant.now());
    }

    public WalletTransactionType type() {
        return type;
    }

    public Money amount() {
        return amount;
    }

    public String reference() {
        return reference;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
