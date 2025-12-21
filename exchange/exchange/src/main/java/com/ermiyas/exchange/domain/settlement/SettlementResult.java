package com.ermiyas.exchange.domain.settlement;

import java.util.Collections;
import java.util.List;

/**
 * Captures which users were credited/debited during settlement.
 */
public final class SettlementResult {
    private final long outcomeId;
    private final List<Long> creditedUserIds;
    private final List<Long> debitedUserIds;

    public SettlementResult(long outcomeId, List<Long> creditedUserIds, List<Long> debitedUserIds) {
        this.outcomeId = outcomeId;
        this.creditedUserIds = List.copyOf(creditedUserIds);
        this.debitedUserIds = List.copyOf(debitedUserIds);
    }

    public long outcomeId() {
        return outcomeId;
    }

    public List<Long> creditedUserIds() {
        return Collections.unmodifiableList(creditedUserIds);
    }

    public List<Long> debitedUserIds() {
        return Collections.unmodifiableList(debitedUserIds);
    }
}
