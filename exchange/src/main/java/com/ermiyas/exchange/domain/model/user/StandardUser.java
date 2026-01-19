package com.ermiyas.exchange.domain.model.user;
import com.ermiyas.exchange.domain.model.Wallet;
import jakarta.persistence.*;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Password;
import lombok.*;

@Entity
@DiscriminatorValue("STANDARD")
@NoArgsConstructor
public class StandardUser extends User implements WalletOwner {

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet; // OOP: Financial capability limited to players

    public StandardUser(String username, String email, Password password) {
        super(username, email, password);
    }
    @Override
    public Wallet getWallet() {
        return this.wallet;
    }
    @Override
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String getRoleName() { return "STANDARD_PLAYER"; }

    @Override
    public void validateTransaction(Money amount) {
        //If needed I can implement restrictions on the account of standard user
    }
}