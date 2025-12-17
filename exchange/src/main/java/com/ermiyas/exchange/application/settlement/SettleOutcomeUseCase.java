package com.ermiyas.exchange.application.settlement;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import com.ermiyas.exchange.domain.settlement.ActualOutcome;
import com.ermiyas.exchange.domain.settlement.SettledBet;
import com.ermiyas.exchange.domain.settlement.SettledBetFactory;
import com.ermiyas.exchange.domain.settlement.SettlementResult;
import com.ermiyas.exchange.domain.wallet.WalletService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Settlement now actually moves funds using the wallet service.
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

            walletService.credit(winnerUserId, agreement.totalPayout(), "Bet Settlement for outcome: "+outcomeId);
        
        agreement.markSettled();
        betAgreementRepository.save(agreement);
        }
    }

    
}
