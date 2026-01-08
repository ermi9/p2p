package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.model.user.StandardUser; 
import com.ermiyas.exchange.domain.model.user.AdminUser;    
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JpaUserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            

            User standardUser = new StandardUser("test_user", "test@example.com");
            userRepository.save(standardUser);

            Wallet standardWallet = new Wallet(standardUser, Money.of(new BigDecimal("1000.00")));
            walletRepository.save(standardWallet);

            User adminUser = new AdminUser("admin_user", "admin@exchange.com");
            userRepository.save(adminUser);

            Wallet adminWallet = new Wallet(adminUser, Money.of(new BigDecimal("5000.00")));
            walletRepository.save(adminWallet);

            System.out.println(">>> Test Data Initialized:");
            System.out.println(">>> Created StandardUser 'test_user' with $1000.");
            System.out.println(">>> Created AdminUser 'admin_user' with $5000.");
        }
    }
}