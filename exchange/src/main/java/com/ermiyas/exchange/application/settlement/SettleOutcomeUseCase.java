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

/**
 * Settlement now actually moves funds using the wallet service.
 */
public class SettleOutcomeUseCase {
    private final BetAgreementRepository betAgreementRepository;
    private final WalletService walletService;

    public SettleOutcomeUseCase(BetAgreementRepository betAgreementRepository,
                                WalletService walletService) {
        this.betAgreementRepository = betAgreementRepository;
        this.walletService = walletService;
    }

    public SettlementResult execute(long outcomeId, ActualOutcome outcome) {
        List<BetAgreement> agreements = betAgreementRepository.findByOutcomeId(outcomeId);
        List<Long> credited = new ArrayList<>();
        List<Long> debited = new ArrayList<>();

        for (BetAgreement agreement : agreements) {
            SettledBet settledBet = SettledBetFactory.from(agreement);
            Money payout = settledBet.payout();
            String reference = "Settlement for offer " + agreement.offerId();

            if (settledBet.isWinning(outcome)) {
                walletService.creditForBet(settledBet.makerUserId(), payout, reference);
                walletService.debitForBet(agreement.takerUserId(), payout, reference);
                credited.add(settledBet.makerUserId());
                debited.add(agreement.takerUserId());
            } else {
                walletService.creditForBet(agreement.takerUserId(), payout, reference);
                walletService.debitForBet(settledBet.makerUserId(), payout, reference);
                credited.add(agreement.takerUserId());
                debited.add(settledBet.makerUserId());
            }
        }

        return new SettlementResult(outcomeId, credited, debited);
    }
}
