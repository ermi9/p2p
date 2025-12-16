package com.ermiyas.exchange;

import com.ermiyas.exchange.application.offer.CreateOfferUseCase;
import com.ermiyas.exchange.application.orderbook.TakeOfferUseCase;
import com.ermiyas.exchange.application.settlement.SettleOutcomeUseCase;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Position;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;
import com.ermiyas.exchange.domain.wallet.Wallet;
import com.ermiyas.exchange.domain.wallet.WalletService;
import com.ermiyas.exchange.infrastructure.persistence.inmemory.InMemoryBetAgreementRepository;
import com.ermiyas.exchange.infrastructure.persistence.inmemory.InMemoryOfferRepository;
import com.ermiyas.exchange.infrastructure.persistence.inmemory.InMemoryOrderBookRepository;
import com.ermiyas.exchange.infrastructure.persistence.inmemory.InMemoryWalletRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication // Added so SpringBootTest can locate the application configuration.
public class ExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeApplication.class, args);
        runDemoScenario();
    }

    private static void runDemoScenario() {
        // -----------------------------
        // 1. Repositories (in-memory)
        // -----------------------------
        InMemoryOfferRepository offerRepository =
                new InMemoryOfferRepository();

        InMemoryOrderBookRepository orderBookRepository =
                new InMemoryOrderBookRepository();

        InMemoryBetAgreementRepository betAgreementRepository =
                new InMemoryBetAgreementRepository();

        InMemoryWalletRepository walletRepository =
                new InMemoryWalletRepository();

        // -----------------------------
        // 2. Wallets + initial funds
        // -----------------------------
        Wallet user1Wallet = new Wallet(
                1L,
                new Money(new BigDecimal("100.00"))
        );

        Wallet user2Wallet = new Wallet(
                2L,
                new Money(new BigDecimal("100.00"))
        );

        walletRepository.save(user1Wallet);
        walletRepository.save(user2Wallet);

        System.out.println("Initial balances:");
        System.out.println("User 1: " + user1Wallet.balance().value());
        System.out.println("User 2: " + user2Wallet.balance().value());
        System.out.println();

        // -----------------------------
        // 3. Use cases
        // -----------------------------
        CreateOfferUseCase createOffer =
                new CreateOfferUseCase(
                        offerRepository,
                        orderBookRepository
                );

        TakeOfferUseCase takeOffer =
                new TakeOfferUseCase(
                        orderBookRepository,
                        offerRepository,
                        betAgreementRepository
                );

        WalletService walletService =
                new WalletService(walletRepository);

        SettleOutcomeUseCase settleOutcome =
                new SettleOutcomeUseCase(
                        betAgreementRepository,
                        walletService
                );

        // -----------------------------
        // 4. User 1 creates an offer
        // -----------------------------
        long outcomeId = 10L;

        createOffer.execute(
                100L,                   // offerId
                1L,                     // makerUserId
                outcomeId,
                Position.FOR,
                new Odds(new BigDecimal("2.00")),
                new Money(new BigDecimal("50.00"))
        );

        System.out.println("Offer created by User 1 (FOR, stake 50 @ odds 2.0)");
        System.out.println();

        // -----------------------------
        // 5. User 2 takes part of offer
        // -----------------------------
        takeOffer.execute(
                outcomeId,
                100L,                   // offerId
                2L,                     // takerUserId
                new Money(new BigDecimal("50.00"))
        );

        System.out.println("User 2 took the offer (stake 50)");
        System.out.println();

        // -----------------------------
        // 6. Settle outcome
        // -----------------------------
        settleOutcome.execute(
                outcomeId,
                ActualOutcome.OUTCOME_HAPPENED
        );

        // -----------------------------
        // 7. Final balances
        // -----------------------------
        System.out.println("Final balances after settlement:");
        System.out.println("User 1: " +
                walletRepository.findByUserId(1L).balance().value());
        System.out.println("User 2: " +
                walletRepository.findByUserId(2L).balance().value());
    }
}
