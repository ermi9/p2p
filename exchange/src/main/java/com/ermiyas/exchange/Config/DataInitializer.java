package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.model.user.*;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            try {
                // UserFactory.createAdmin returns UserInterface; cast to User for the Repository
                User admin = (User) UserFactory.createAdmin("admin_boss", "admin@exchange.com", "admin_pass");
                userRepository.save(admin);

                // Cast to StandardUser to access setWallet/getWallet methods
                StandardUser maker = (StandardUser) UserFactory.createStandard("maker_user", "maker@test.com", "password");
                userRepository.save(maker);
                createAndLinkWallet(maker, "5000.00");

                StandardUser taker = (StandardUser) UserFactory.createStandard("taker_user", "taker@test.com", "password");
                userRepository.save(taker);
                createAndLinkWallet(taker, "2000.00");

                System.out.println(">>> Environment Seeded Successfully.");
            } catch (ExchangeException e) {
                System.err.println(">>> Seed Failed: " + e.getMessage());
            }
        }
    }

    private void createAndLinkWallet(StandardUser user, String amount) throws ExchangeException {
        // Money.of(BigDecimal) performs validation and scaling
        Wallet wallet = new Wallet(user, Money.of(new BigDecimal(amount)));
        user.setWallet(wallet); // Maintain bidirectional relationship
        walletRepository.save(wallet);
    }
}