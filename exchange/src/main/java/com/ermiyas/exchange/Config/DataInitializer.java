package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.model.User;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.WalletRepository;
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
            // Create a Test User
            User user = User.builder()
                    .username("test_user")
                    .email("test@example.com")
                    .build();
            
            userRepository.save(user);

            // Create their Wallet with $1,000
            Wallet wallet = new Wallet(user, Money.of(new BigDecimal("1000.00")));
            walletRepository.save(wallet);

            System.out.println(">>> Test Data Initialized: user 'test_user' created with $1000 balance.");
        }
    }
}