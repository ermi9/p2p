package com.ermiyas.exchange.domain.wallet;
import com.ermiyas.exchange.common.Money;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/*
wallet enforces balance >= 0. Added stricter validation & correct transaction typing.
*/
public final class Wallet {
    private final long userId;
    
    private Money totalBalance;
    
    private Money reservedBalance;
    
    private final List<WalletTransaction> transactions;


    public Wallet(long userId,Money startingBalance){
        this.userId=userId;
        this.totalBalance=Objects.requireNonNull(startingBalance,"startingaBalance");
        this.transactions=new ArrayList<>();
        this.reservedBalance=Money.zero();
    }
    
    public long userId(){
        return userId;
    }
    
    public Money availableBalance(){
        return totalBalance.minus(reservedBalance);

    }
    //reserved and available balance logic added
    public void reserve(Money amount){
        requirePositive(amount);

        if(amount.compareTo(availableBalance())>0){
            throw new InsufficientFundsException(availableBalance(),amount);
        }
        reservedBalance=reservedBalance.plus(amount);
    }

    //release the reserved amount
    public void release(Money amount){
        requirePositive(amount);

        if(amount.compareTo(reservedBalance)>0)
            throw new IllegalStateException("Cannot release more than reserved balance");
    
        reservedBalance=reservedBalance.minus(amount);
    
    }


    public Money totalbalance(){
        return totalBalance;
    }
    public List<WalletTransaction> transactions(){
        return Collections.unmodifiableList(transactions);
    }
    //deposit and withdraw logic
    public WalletTransaction deposit(Money amount,String reference){
        requirePositive(amount);
        totalBalance=totalBalance.plus(amount);
        WalletTransaction tx=WalletTransaction.of(userId,WalletTransactionType.DEPOSIT,amount,reference);
        transactions.add(tx);
        return tx;
    }
    public WalletTransaction withdraw(Money amount, String reference){
        requirePositive(amount);
        totalBalance=totalBalance.minus(amount);
        WalletTransaction tx=WalletTransaction.of(userId,WalletTransactionType.WITHDRAWAL,amount,reference);
        transactions.add(tx);
        return tx;

    }

    
    private void requirePositive(Money amount){
        Objects.requireNonNull(amount, "amount");
        if(amount.value().signum()<=0)
            throw new IllegalArgumentException("Amount must be positive!");

    }

}
