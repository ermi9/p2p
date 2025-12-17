package com.ermiyas.exchange.application;

import com.ermiyas.exchange.application.ports.*;
import com.ermiyas.exchange.application.settlement.SettleOutcomeUseCase;
import com.ermiyas.exchange.application.orderbook.TakeOfferUseCase;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.Position;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;
import com.ermiyas.exchange.domain.wallet.Wallet;
import com.ermiyas.exchange.domain.wallet.WalletService;
import com.ermiyas.exchange.domain.orderbook.OrderBook;
import com.ermiyas.exchange.infrastructure.persistence.inmemory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BettingIntegrationTest {
    private WalletService walletService;
    private TakeOfferUseCase takeOfferUseCase;
    private SettleOutcomeUseCase settleOutcomeUseCase;
    private WalletRepository walletRepository;
    private OfferRepository offerRepo;

    @BeforeEach
    void setup() {
        walletRepository = new InMemoryWalletRepository();
        walletService = new WalletService(walletRepository);
        
        BetAgreementRepository betRepo = new InMemoryBetAgreementRepository();
        offerRepo = new InMemoryOfferRepository();
        
        // FIX 1: Correct constructor order (WalletService, OfferRepo, BetRepo)
        takeOfferUseCase = new TakeOfferUseCase(walletService, offerRepo, betRepo);
        settleOutcomeUseCase = new SettleOutcomeUseCase(betRepo, walletService);

        // Setup Users
        walletRepository.save(new Wallet(1L, new Money(new BigDecimal("100.00"))));
        walletRepository.save(new Wallet(2L, new Money(new BigDecimal("100.00"))));

        // Setup an Offer in the repo (ID: 101, Maker: 1, Outcome: 1)
        Money stake = new Money(new BigDecimal("10.00"));
        Odds odds = new Odds(new BigDecimal("3.0"));
        Offer offer = new Offer(101L, 1L, 1L, Position.FOR, odds, stake);
        offerRepo.save(offer);
    }

    @Test
    void testMakerWinsWhenOutcomeHappens() {
        Money stake = new Money(new BigDecimal("10.00"));
        
        // FIX 2: Execute now only takes 3 arguments: (offerId, takerUserId, stake)
        takeOfferUseCase.execute(101L, 2L, stake);

        // Settle
        settleOutcomeUseCase.execute(1L, ActualOutcome.OUTCOME_HAPPENED);

        BigDecimal makerFinal = walletService.totalBalance(1L).value().stripTrailingZeros();
        BigDecimal takerFinal = walletService.totalBalance(2L).value().stripTrailingZeros();

        assertEquals(0, new BigDecimal("120").compareTo(makerFinal), "Maker should have 120");
        assertEquals(0, new BigDecimal("80").compareTo(takerFinal), "Taker should have 80");
    }

    @Test
    void testTakerWinsWhenOutcomeDoesNotHappen() {
        Money stake = new Money(new BigDecimal("10.00"));
        
        // Match
        takeOfferUseCase.execute(101L, 2L, stake);

        // Settle (Against wins)
        settleOutcomeUseCase.execute(1L, ActualOutcome.OUTCOME_DID_NOT_HAPPEN);

        BigDecimal makerFinal = walletService.totalBalance(1L).value().stripTrailingZeros();
        BigDecimal takerFinal = walletService.totalBalance(2L).value().stripTrailingZeros();

        assertEquals(0, new BigDecimal("90").compareTo(makerFinal), "Maker should have 90");
        assertEquals(0, new BigDecimal("110").compareTo(takerFinal), "Taker should have 110");
    }
}
