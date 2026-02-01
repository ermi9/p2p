package com.ermiyas.exchange.domain.model.user;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Password;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;


@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
public class AdminUser extends User {

    public AdminUser(String username, String email, Password password) {
        super(username, email, password);
    }

    @Override
    public String getRoleName() { return "EXCHANGE_ADMIN"; }

    @Override
    public void validateTransaction(Money amount)  {
        // Admins might validate system wide moves, but they have no personal wallet to check
    }
    @Override
    public Wallet getWallet() throws ExchangeException{
        throw new IllegalBetException("Admin users cannot own a wallet");

    }
}