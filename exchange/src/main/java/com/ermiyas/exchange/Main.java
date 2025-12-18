package com.ermiyas.exchange; // THIS MUST MATCH THE DIRECTORY

import com.ermiyas.exchange.application.orderbook.TakeOfferUseCase;
import com.ermiyas.exchange.application.settlement.SettleOutcomeUseCase;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.offer.Position;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;
import com.ermiyas.exchange.domain.wallet.Wallet;
import com.ermiyas.exchange.domain.wallet.WalletService;
import com.ermiyas.exchange.infrastructure.persistence.inmemory.*;
import com.ermiyas.exchange.application.ports.*;

import java.math.BigDecimal;
import java.util.Map;

public class Main {
    private static final Map<Long, String> NAMES = Map.of(
        1L, "Alice (Maker)",
        2L, "Bob (Taker A)",
        3L, "Charlie (Taker B)"
    );

    public static void main(String[] args) {
        WalletRepository walletRepo = new InMemoryWalletRepository();
        OfferRepository offerRepo = new InMemoryOfferRepository();
        BetAgreementRepository betRepo = new InMemoryBetAgreementRepository();
        
        WalletService walletService = new WalletService(walletRepo);
        TakeOfferUseCase takeOfferUseCase = new TakeOfferUseCase(walletService, offerRepo, betRepo);
        SettleOutcomeUseCase settleOutcomeUseCase = new SettleOutcomeUseCase(betRepo, walletService);

        walletRepo.save(new Wallet(1L, new Money(new BigDecimal("1000.00")))); 
        walletRepo.save(new Wallet(2L, new Money(new BigDecimal("500.00"))));  
        walletRepo.save(new Wallet(3L, new Money(new BigDecimal("500.00"))));  

        long offerId = 101L;
        long outcomeId = 1L;
        BigDecimal stakeVal = new BigDecimal("200.00");
        BigDecimal oddsVal = new BigDecimal("4.0");
        
        Offer offer = new Offer(offerId, 1L, outcomeId, Position.FOR, new Odds(oddsVal), new Money(stakeVal));
        offerRepo.save(offer);
        walletService.reserve(1L, offer.remainingStake());
        
        printSystemStatus("1. START: Alice posts $200 Stake", walletService, offer);

        takeOfferUseCase.execute(offerId, 2L, new Money(new BigDecimal("50.00")));
        printSystemStatus("2. MATCH: Bob takes $50", walletService, offer);

        takeOfferUseCase.execute(offerId, 3L, new Money(new BigDecimal("150.00")));
        printSystemStatus("3. MATCH: Charlie takes $150", walletService, offer);

        System.out.println("\n>>> FINAL EVENT: Grimsby Wins! Alice collects winnings. <<<");
        settleOutcomeUseCase.execute(outcomeId, ActualOutcome.OUTCOME_HAPPENED);

        printSystemStatus("4. FINAL: Settlement Complete", walletService, offer);
    }

    private static void printSystemStatus(String label, WalletService ws, Offer offer) {
        System.out.println("\n--- " + label + " ---");
        System.out.printf("%-18s | %-12s | %-12s | %-12s%n", "User Name", "Total", "Available", "Reserved");
        System.out.println("--------------------------------------------------------------------------");
        for (long id : NAMES.keySet().stream().sorted().toList()) {
            BigDecimal total = ws.totalBalance(id).value();
            BigDecimal avail = ws.availableBalance(id).value();
            BigDecimal reserved = total.subtract(avail);
            System.out.printf("%-18s | $%-11s | $%-11s | $%-11s%n", NAMES.get(id), total.toPlainString(), avail.toPlainString(), reserved.toPlainString());
        }
    }
}