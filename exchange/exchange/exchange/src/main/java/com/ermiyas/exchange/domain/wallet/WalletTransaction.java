package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.common.Money;

import java.time.Instant;
import java.util.Objects;

/**
 * Small immutable record describing what movement happened.
 */
public final class WalletTransaction {
    private final long userId;
    private final WalletTransactionType type;
    private final Money amount;
    private final String reference;
    private final Instant createdAt;

    private WalletTransaction(
            long userId,
            WalletTransactionType type,
            Money amount,
            String reference,
            Instant createdAt
    ) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public static WalletTransaction of(
            long userId,
            WalletTransactionType type,
            Money amount,
            String reference
    ) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(type, "type");
        return new WalletTransaction(
                userId,
                type,
                amount,
                reference,
                Instant.now()
        );
    }

    public long userId() {
        return userId;
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
