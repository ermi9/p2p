package com.ermiyas.exchange;

import com.ermiyas.exchange.mocks.WalletRepositoryMock;
import com.ermiyas.exchange.mocks.OfferRepositoryMock;
import com.ermiyas.exchange.mocks.AgreementRepositoryMock;
// ... (rest of your imports)
import com.ermiyas.exchange.application.offer.CreateOfferUseCase;
import com.ermiyas.exchange.application.orderbook.TakeOfferUseCase;
import com.ermiyas.exchange.application.settlement.SettleOutcomeUseCase;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;
import com.ermiyas.exchange.domain.wallet.Wallet;
import com.ermiyas.exchange.domain.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BettingEngineTest {
    // We will use real services but a Mock/In-Memory Repository for the test
    private WalletService walletService;
    private CreateOfferUseCase createOfferUseCase;
    private TakeOfferUseCase takeOfferUseCase;
    private SettleOutcomeUseCase settleOutcomeUseCase;

    // Test Constants
    private final long ERMIYAS_ID = 1L;
    private final long BOB_ID = 2L;
    private final long SYSTEM_ID = 0L;
    private final long OUTCOME_ID = 99L;

    @BeforeEach
    void setup() {
        // 1. Setup In-Memory Repositories (Simplified for this example)
        WalletRepositoryMock walletRepo = new WalletRepositoryMock();
        OfferRepositoryMock offerRepo = new OfferRepositoryMock();
        AgreementRepositoryMock agreementRepo = new AgreementRepositoryMock();

        // 2. Initialize Wallets
        walletRepo.save(new Wallet(ERMIYAS_ID, Money.of("1000.00")));
        walletRepo.save(new Wallet(BOB_ID, Money.of("1000.00")));
        walletRepo.save(new Wallet(SYSTEM_ID, Money.of("0.00")));

        // 3. Initialize Services
        walletService = new WalletService(walletRepo);
        createOfferUseCase = new CreateOfferUseCase(offerRepo, walletService);
        takeOfferUseCase = new TakeOfferUseCase(walletService, offerRepo, agreementRepo);
        settleOutcomeUseCase = new SettleOutcomeUseCase(agreementRepo, walletService);
    }

    @Test
    void testFullChallengerCycle_MakerWins() {
        // --- STEP 1: CREATE CHALLENGE ---
        // Ermiyas challenges at 4.0 odds with a $100 stake
        long offerId = createOfferUseCase.execute(ERMIYAS_ID, OUTCOME_ID, Odds.of("4.0"), Money.of("100.00"));

        // Verify Reservation: Ermiyas should have $900 available, $100 reserved, $1000 total
        assertEquals(Money.of("900.00"), walletService.availableBalance(ERMIYAS_ID));
        assertEquals(Money.of("1000.00"), walletService.totalBalance(ERMIYAS_ID));

        // --- STEP 2: TAKE CHALLENGE ---
        // Bob matches the $100 challenge. At 4.0 odds, Bob's liability is $300.
        takeOfferUseCase.execute(offerId, BOB_ID, Money.of("100.00"));

        // Verify Bob's Reservation: $1000 - $300 liability = $700 available
        assertEquals(Money.of("700.00"), walletService.availableBalance(BOB_ID));

        // --- STEP 3: SETTLEMENT ---
        // The outcome happens! Ermiyas (Challenger) wins.
        settleOutcomeUseCase.execute(OUTCOME_ID, ActualOutcome.OUTCOME_HAPPENED);

        // --- STEP 4: VERIFY FINAL BALANCES ---
        // 1. Bob (Loser): Lost $300. Total should be $700.
        assertEquals(Money.of("700.00"), walletService.totalBalance(BOB_ID));
        assertEquals(Money.of("0.00"), walletService.findByUserId(BOB_ID).totalBalance().minus(walletService.availableBalance(BOB_ID)), "Bob should have no reserved money left");

        // 2. System (Commission): 5% of Bob's $300 = $15.00
        assertEquals(Money.of("15.00"), walletService.totalBalance(SYSTEM_ID));

        // 3. Ermiyas (Winner): 
        // Original $1000 + ($300 Profit - $15 Commission) = $1285.00
        assertEquals(Money.of("1285.00"), walletService.totalBalance(ERMIYAS_ID));
        assertEquals(Money.of("1285.00"), walletService.availableBalance(ERMIYAS_ID), "Ermiyas's winnings should be fully available");
    }
}