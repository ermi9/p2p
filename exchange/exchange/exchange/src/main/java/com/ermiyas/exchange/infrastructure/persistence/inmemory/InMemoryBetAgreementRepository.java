package com.ermiyas.exchange.infrastructure.persistence.inmemory;
import com.ermiyas.exchange.application.ports.BetAgreementRepository;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBetAgreementRepository implements BetAgreementRepository{
    private final Map<Long,List<BetAgreement>>byOutcomeId=new HashMap<>();
    @Override
    public void save(BetAgreement agreement){
        long outcomeId=agreement.outcomeId();
        List<BetAgreement> list=byOutcomeId.get(outcomeId);
        if(list ==null){
            list=new ArrayList<>();
            byOutcomeId.put(outcomeId,list);
        }
        list.add(agreement);
    }
    @Override
    public List<BetAgreement> findByOutcomeId(long outcomeId){
        List<BetAgreement> list=byOutcomeId.get(outcomeId);
        if(list==null){
            return new ArrayList<>();//return empty list if the list is null(empty)
        }
        return new ArrayList<>(list);
    }
}
