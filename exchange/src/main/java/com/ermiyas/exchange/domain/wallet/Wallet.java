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
    private Money balance;
    private final List<WalletTransaction> transactions;
    public Wallet(long userId,Money startingBalance){
        this.userId=userId;
        this.balance=Objects.requireNonNull(startingBalance,"startingaBalance");
        this.transactions=new ArrayList<>();
    }
    public long userId(){
        return userId;
    }
    public Money balance(){
        return balance;
    }
    public List<WalletTransaction> transactions(){
        return Collections.unmodifiableList(transactions);
    }
    public WalletTransaction deposit(Money amount,String reference){
        requirePositive(amount);
        balance=balance.plus(amount);
        WalletTransaction tx=WalletTransaction.of(userId,WalletTransactionType.DEPOSIT,amount,reference);
        transactions.add(tx);
        return tx;
    }
    public WalletTransaction withdraw(Money amount, String reference){
        requirePositive(amount);
        ensureSufficientFunds(amount);
        balance=balance.minus(amount);
        WalletTransaction tx=WalletTransaction.of(userId,WalletTransactionType.WITHDRAWAL,amount,reference);
        transactions.add(tx);
        return tx;

    }

    public WalletTransaction debitForBet(Money amount, String reference){
        requirePositive(amount);
        ensureSufficientFunds(amount);
        balance=balance.minus(amount);
        WalletTransaction tx=WalletTransaction.of(userId,WalletTransactionType.BET_DEBIT,amount,reference);
        transactions.add(tx);
        return tx;

    }
    public WalletTransaction creditForBet(Money amount, String reference){
        requirePositive(amount);
        balance=balance.plus(amount);
        WalletTransaction tx=WalletTransaction.of(userId,WalletTransactionType.BET_CREDIT,amount,reference);
        transactions.add(tx);
        return tx;
    }
    private void ensureSufficientFunds(Money amount){
        //compare values
        if(balance.value().compareTo(amount.value())<0)
            throw new InsufficientFundsException(balance, amount);

    }
    private void requirePositive(Money amount){
        Objects.requireNonNull(amount, "amount");
        if(amount.value().signum()<=0)
            throw new IllegalArgumentException("Amount must be positive!");

    }

}
