package com.ermiyas.exchange.application.settlement;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;

import com.ermiyas.exchange.domain.wallet.WalletService;

import java.util.List;
import java.util.Objects;
import java.math.BigDecimal;

/**
 * Settlement now actually performs real wallet operations (release + credit).
 */
public class SettleOutcomeUseCase {
    private final BetAgreementRepository betAgreementRepository;
    private final WalletService walletService;
    // per the "House" logic: 5% comission on net profit
    private static final BigDecimal COMMISION_RATE=new BigDecimal("0.05");
    private static final long SYSTEM_PLATFORM_ID=0L;

    public SettleOutcomeUseCase(BetAgreementRepository betAgreementRepository,
                                WalletService walletService) {
        this.betAgreementRepository = Objects.requireNonNull(betAgreementRepository);
        this.walletService = Objects.requireNonNull(walletService);
    }

    public void execute(long outcomeId,ActualOutcome outcome){

        List<BetAgreement> agreements=betAgreementRepository.findByOutcomeId(outcomeId);

        for(BetAgreement agreement: agreements){
            //skip if already settled
            if(agreement.isSettled()) continue;

            long winnerUserId=agreement.winnerUserId(outcome);
            long loserUserId=agreement.loserUserId(outcome);

            //Identify risks(Money already reserved in Wallets)
            Money winnerRisk=(winnerUserId==agreement.makerUserId()) ? agreement.makerRisk() : agreement.takerRisk();
            Money grossProfit=(winnerUserId==agreement.makerUserId()) ? agreement.takerRisk():agreement.makerRisk();

            //calculate platform's commision
            Money commision=grossProfit.multiply(COMMISION_RATE);
            Money netProfit=grossProfit.minus(commision);

            //This releases the loser's reserved money and removes it from their balance permanently
            walletService.debitForBet(loserUserId,grossProfit,"Lost Challenge: "+agreement.offerId());

            walletService.creditForBet(winnerUserId, netProfit, winnerRisk,"Won challenge");

            walletService.credit(SYSTEM_PLATFORM_ID,commision,"Commission from challenge: " +agreement.offerId());


            //update agreemnent state
            agreement.markSettled();
            betAgreementRepository.save(agreement);
   
        }
    }

    
}
