package com.ermiyas.exchange.domain.model; 

import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.exception.InsufficientFundsException; 
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.user.User; 
import jakarta.persistence.*;
import lombok.*;

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


    public Money availableBalance() {

        try {
            return totalBalance.minus(reservedBalance);
        }
        
        catch (IllegalBetException e) {
            throw new IllegalStateException("Critical Consistency Error: Reserved exceeds Total", e);
        }    
    }


    public void reserve(Money amount) throws InsufficientFundsException {
        if (amount.isGreaterThan(availableBalance())) {
            throw new InsufficientFundsException("Reservation failed: " + amount + " exceeds available balance.");
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
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.totalBalance = this.totalBalance.plus(amount);
    }


    public void unreserve(Money amount) throws ExchangeException {
        validateReservation(amount);
        this.reservedBalance = this.reservedBalance.minus(amount);
    }


    private void validateReservation(Money amount) throws ExchangeException {
        if (amount.isGreaterThan(reservedBalance)) {
            throw new IllegalBetException("Financial Integrity Error: Cannot release " + amount + " as it exceeds reserved funds.");
        }
    }
}