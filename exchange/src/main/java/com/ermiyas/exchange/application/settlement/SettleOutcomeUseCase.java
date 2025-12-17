package com.ermiyas.exchange.application.settlement;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;

import com.ermiyas.exchange.domain.wallet.WalletService;

import java.util.List;
import java.util.Objects;

/**
 * Settlement now actually performs real wallet operations (release + credit).
 */
public class SettleOutcomeUseCase {
    private final BetAgreementRepository betAgreementRepository;
    private final WalletService walletService;

    public SettleOutcomeUseCase(BetAgreementRepository betAgreementRepository,
                                WalletService walletService) {
        this.betAgreementRepository = Objects.requireNonNull(betAgreementRepository);
        this.walletService = Objects.requireNonNull(walletService);
    }

    public void execute(long outcomeId,ActualOutcome outcome){

        List<BetAgreement> agreements=betAgreementRepository.findByOutcomeId(outcomeId);

        for(BetAgreement agreement: agreements){
            long winnerUserId=agreement.winnerUserId(outcome);
            long loserUserId=agreement.loserUserId(outcome);

            //release both reservations

            walletService.release(agreement.makerUserId(),agreement.makerRisk());
            walletService.release(agreement.takerUserId(),agreement.takerRisk());

walletService.withdraw(loserUserId, 
                                  winnerUserId == agreement.makerUserId() ? agreement.takerRisk() : agreement.makerRisk(), 
                                  "Bet Lost for outcome: " + outcomeId);
        walletService.credit(winnerUserId, 
                                 winnerUserId == agreement.makerUserId() ? agreement.takerRisk() : agreement.makerRisk(), 
                                 "Bet Won for outcome: " + outcomeId);
        agreement.markSettled();
        betAgreementRepository.save(agreement);
        }
    }

    
}
