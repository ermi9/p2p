package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.common.Money;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Wallet aggregate root.
 *
 * Invariants:
 * - totalBalance >= 0
 * - reservedBalance >= 0
 * - reservedBalance <= totalBalance
 */
public final class Wallet {

    private Money totalBalance;
    private Money reservedBalance;
    private final List<WalletTransaction> transactions = new ArrayList<>();

    public Wallet(Money startingBalance) {
        this.totalBalance = Objects.requireNonNull(startingBalance, "startingBalance");
        this.reservedBalance = Money.zero();
    }

    // --------------------
    // Balance views
    // --------------------

    public Money totalBalance() {
        return totalBalance;
    }

    public Money reservedBalance() {
        return reservedBalance;
    }

    public Money availableBalance() {
        return totalBalance.minus(reservedBalance);
    }

    // --------------------
    // Cash operations
    // --------------------

    public WalletTransaction deposit(Money amount, String reference) {
        requirePositive(amount);
        Objects.requireNonNull(reference, "reference");

        totalBalance = totalBalance.plus(amount);

        WalletTransaction tx = WalletTransaction.of(WalletTransactionType.DEPOSIT, amount, reference);
        transactions.add(tx);
        return tx;
    }

    public WalletTransaction withdraw(Money amount, String reference) {
        requirePositive(amount);
        Objects.requireNonNull(reference, "reference");

        if (amount.compareTo(availableBalance()) > 0) {
            throw new InsufficientFundsException(availableBalance(), amount);
        }

        totalBalance = totalBalance.minus(amount);

        WalletTransaction tx = WalletTransaction.of(WalletTransactionType.WITHDRAWAL, amount, reference);
        transactions.add(tx);
        return tx;
    }

    // --------------------
    // Betting flows
    // --------------------

    /**
     * Finalizes a bet debit.
     * Assumes the stake was previously reserved.
     */
    public void debitForBet(Money amount, String reference) {
        requirePositive(amount);
        Objects.requireNonNull(reference, "reference");

        if (amount.compareTo(reservedBalance) > 0) {
            throw new IllegalStateException("Bet debit exceeds reserved funds");
        }

        // unlock first, then deduct from total
        release(amount);
        totalBalance = totalBalance.minus(amount);

        transactions.add(
                WalletTransaction.of(
                        WalletTransactionType.BET_DEBIT,
                        amount,
                        reference
                )
        );
    }

    /**
     * Credits winnings from a settled bet fill.
     * (Winnings exclude stake)
     */
    public WalletTransaction creditBet(Money netProfit, String reference) {
        requirePositive(netProfit);
        Objects.requireNonNull(reference, "reference");

        totalBalance = totalBalance.plus(netProfit);

        WalletTransaction tx = WalletTransaction.of(WalletTransactionType.BET_CREDIT, netProfit, reference);
        transactions.add(tx);
        return tx;
    }

    // --------------------
    // Reservation logic
    // --------------------

    public void reserve(Money amount) {
        requirePositive(amount);
        if (amount.compareTo(availableBalance()) > 0) {
            throw new InsufficientFundsException(availableBalance(), amount);
        }

        reservedBalance = reservedBalance.plus(amount);
    }

    public void release(Money amount) {
        requirePositive(amount);
        if (amount.compareTo(reservedBalance) > 0) {
            throw new IllegalStateException("Release exceeds reserved balance");
        }

        reservedBalance = reservedBalance.minus(amount);
    }

    // --------------------
    // History
    // --------------------

    public List<WalletTransaction> transactions() {
        return Collections.unmodifiableList(transactions);
    }

    // --------------------
    // Helpers
    // --------------------

    private void requirePositive(Money amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.value().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
