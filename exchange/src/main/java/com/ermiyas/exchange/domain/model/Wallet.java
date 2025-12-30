package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * PURE OOP: Wallet Aggregate Root.
 * Implements the Reservation Pattern to manage user funds.
 * Total Balance = Available + Reserved.
 */
@Entity
@Table(name = "wallets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_balance", nullable = false))
    private Money totalBalance;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "reserved_balance", nullable = false))
    private Money reservedBalance;

    public Wallet(User user, Money initialBalance) {
        this.user = Objects.requireNonNull(user);
        this.totalBalance = Objects.requireNonNull(initialBalance);
        this.reservedBalance = Money.zero();
    }

    /**
     * Business Logic: Returns the amount the user can actually use for new bets.
     */
    public Money availableBalance() {
        return totalBalance.minus(reservedBalance);
    }

    /**
     * Logic: Reserves a specific amount for a pending bet.
     * Decreases available balance without changing total balance yet.
     */
    public void reserve(Money amount) {
        if (amount.compareTo(availableBalance()) > 0) {
            throw new IllegalStateException("Insufficient available funds to reserve " + amount);
        }
        this.reservedBalance = this.reservedBalance.plus(amount);
    }

    /**
     * Logic: Handles the winning scenario.
     * 1. Release the original risk (reservedBalance -> available).
     * 2. Add the profit from the loser (minus commission).
     */
    public void settleWin(Money stakeToRelease, Money netProfit, CommissionPolicy policy) {
        if (stakeToRelease.compareTo(reservedBalance) > 0) {
            throw new IllegalStateException("Cannot release more than currently reserved");
        }
        
        // Return the reserved risk to the available pool (by reducing reserved amount)
        this.reservedBalance = this.reservedBalance.minus(stakeToRelease);
        
        // Add the gain from the other party minus the platform cut
        Money netGain = policy.apply(netProfit);
        this.totalBalance = this.totalBalance.plus(netGain);
    }

    /**
     * Logic: Handles the losing scenario.
     * Permanently removes the reserved risk from the total balance.
     */
    public void settleLoss(Money stakeToLose) {
        if (stakeToLose.compareTo(reservedBalance) > 0) {
            throw new IllegalStateException("Loss amount exceeds reserved balance");
        }
        
        // Deduct from both to finalize the loss
        this.reservedBalance = this.reservedBalance.minus(stakeToLose);
        this.totalBalance = this.totalBalance.minus(stakeToLose);
    }
    /**
 * Logic: Returns unmatched funds to the available pool.
 * Does not change total balance, just reduces reserved amount.
 */
    public void unreserve(Money amount) {
        if (amount.compareTo(reservedBalance) > 0) {
            throw new IllegalStateException("Cannot unreserve more than currently reserved");
        }
        this.reservedBalance = this.reservedBalance.minus(amount);
    }
}