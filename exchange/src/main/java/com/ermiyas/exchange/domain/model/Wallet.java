package com.ermiyas.exchange.domain.model; 

import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.exception.InsufficientFundsException; 
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.user.User; 
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

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
    @Setter(AccessLevel.NONE) 
    private Money totalBalance;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "reserved_balance", nullable = false))
    @Setter(AccessLevel.NONE) 
    private Money reservedBalance;

    public Wallet(User user, Money initialBalance) {
        this.user = Objects.requireNonNull(user);
        this.totalBalance = Objects.requireNonNull(initialBalance);
        this.reservedBalance = Money.zero();
    }

    /**
     * Calculates the balance available for new bets or withdrawals.
     * Available = Total - Reserved (Escrow).
     */
    @JsonProperty("availableBalance")
    public Money availableBalance() {
        try {
            return totalBalance.minus(reservedBalance);
        } catch (IllegalBetException e) {
            throw new IllegalStateException("Critical Consistency Error: Reserved funds ($" + 
                reservedBalance.value() + ") exceed Total Balance ($" + totalBalance.value() + ")", e);
        }    
    }

    /**
     * Moves funds into Escrow (Reserved) for a pending offer or trade.
     * Provides clear feedback on the deficit if funds are insufficient.
     */
    public void reserve(Money amount) throws InsufficientFundsException {
        Money available = availableBalance();
        if (amount.isGreaterThan(available)) {
            throw new InsufficientFundsException(String.format(
                "Insufficient Funds: Attempted to reserve $%s, but your available balance is only $%s.",
                amount.value(), 
                available.value()
            ));
        }
        this.reservedBalance = this.reservedBalance.plus(amount);
    }

    public void settleWin(Money stakeToRelease, Money netProfit, CommissionPolicy policy) throws ExchangeException {
        validateReservation(stakeToRelease);
        this.reservedBalance = this.reservedBalance.minus(stakeToRelease);
        Money netGain = policy.apply(netProfit);
        this.totalBalance = this.totalBalance.plus(netGain);
    }

    public void settleLoss(Money stakeToLose) throws ExchangeException {
        validateReservation(stakeToLose);
        this.reservedBalance = this.reservedBalance.minus(stakeToLose);
        this.totalBalance = this.totalBalance.minus(stakeToLose);
    }

    public void deposit(Money amount) {
        if (amount.isZero() || amount.isNegative()) {
            throw new IllegalArgumentException("Deposit failed: Amount must be greater than zero.");
        }
        this.totalBalance = this.totalBalance.plus(amount);
    }

    /**
     *  Prevents withdrawal of funds currently tied up in matched bets.
     */
    public void withdraw(Money amount) throws ExchangeException {
        if (amount.isZero() || amount.isNegative()) {
            throw new IllegalArgumentException("Withdrawal failed: Amount must be greater than zero.");
        }
        
        Money available = availableBalance();
        if (amount.isGreaterThan(available)) {
            throw new InsufficientFundsException(String.format(
                "Withdrawal Failed: Attempted to withdraw $%s, but only $%s is available (the rest is locked in active bets).",
                amount.value(), 
                available.value()
            ));
        }
        this.totalBalance = this.totalBalance.minus(amount);
    }

    public void unreserve(Money amount) throws ExchangeException {
        validateReservation(amount);
        this.reservedBalance = this.reservedBalance.minus(amount);
    }

    /**
     *  Ensures we never release more money from escrow than what was locked.
     */
    private void validateReservation(Money amount) throws ExchangeException {
        if (amount.isGreaterThan(reservedBalance)) {
            throw new IllegalBetException(String.format(
                "Financial Integrity Error: Attempted to release $%s from escrow, but only $%s is currently reserved.",
                amount.value(), 
                reservedBalance.value()
            ));
        }
    }
}