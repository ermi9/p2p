package com.ermiyas.exchange.application.ports;

import com.ermiyas.exchange.domain.orderbook.BetAgreement;

import java.util.List;

/**
 * Defined repository contract so the in-memory implementation can actually override it.
 */
public interface BetAgreementRepository {
    void save(BetAgreement agreement);

    List<BetAgreement> findByOutcomeId(long outcomeId);
}
