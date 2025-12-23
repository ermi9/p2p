package com.ermiyas.exchange.mocks;

import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgreementRepositoryMock implements BetAgreementRepository {
    private final Map<Long, BetAgreement> store = new HashMap<>();
    private long idCounter = 1;

    @Override
    public void save(BetAgreement agreement) {
        // Simple mock save logic
        store.put(idCounter++, agreement);
    }

    @Override
    public List<BetAgreement> findByOutcomeId(long outcomeId) {
        return store.values().stream()
                .filter(a -> a.outcomeId() == outcomeId)
                .collect(Collectors.toList());
    }
}