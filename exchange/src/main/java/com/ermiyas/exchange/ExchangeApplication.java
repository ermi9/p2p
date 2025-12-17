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
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class ExchangeApplication {

    public static void main(String[] args) {

        // -----------------------------
        // Infrastructure (in-memory)
        // -----------------------------
        InMemoryOfferRepository offerRepository = new InMemoryOfferRepository();
        InMemoryOrderBookRepository orderBookRepository = new InMemoryOrderBookRepository();
        InMemoryBetAgreementRepository betAgreementRepository = new InMemoryBetAgreementRepository();
        InMemoryWalletRepository walletRepository = new InMemoryWalletRepository();

        // -----------------------------
        // Domain services
        // -----------------------------
        WalletService walletService = new WalletService(walletRepository);

        // -----------------------------
        // Application use cases
        // -----------------------------
        CreateOfferUseCase createOffer =
                new CreateOfferUseCase(offerRepository, orderBookRepository);

        TakeOfferUseCase takeOffer =
                new TakeOfferUseCase(
                        orderBookRepository,
                        offerRepository,
                        betAgreementRepository
                );

        SettleOutcomeUseCase settleOutcome =
                new SettleOutcomeUseCase(
                        betAgreementRepository,
                        walletService
                );

        // -----------------------------
        // Setup wallets
        // -----------------------------
        walletRepository.save(
                new Wallet(1L, new Money(new BigDecimal("100.00")))
        );
        walletRepository.save(
                new Wallet(2L, new Money(new BigDecimal("100.00")))
        );

        System.out.println("Initial balances:");
        printBalances(walletRepository);

        // -----------------------------
        // Scenario
        // -----------------------------
        long outcomeId = 1L;

        // User 1 creates FOR offer
        createOffer.execute(
                1L,                     // offerId
                1L,                     // maker
                outcomeId,
                Position.FOR,
                new Odds(new BigDecimal("2.00")),
                new Money(new BigDecimal("50.00"))
        );

        // User 2 takes full offer
        takeOffer.execute(
                outcomeId,
                1L,
                2L,
                new Money(new BigDecimal("50.00"))
        );

        // Outcome happens
        settleOutcome.execute(
                outcomeId,
                ActualOutcome.OUTCOME_HAPPENED
        );

        System.out.println("\nFinal balances:");
        printBalances(walletRepository);
    }

    private static void printBalances(InMemoryWalletRepository walletRepository) {
        System.out.println("User 1: " +
                walletRepository.findByUserId(1L).balance().value());
        System.out.println("User 2: " +
                walletRepository.findByUserId(2L).balance().value());
    }
}
