package com.ermiyas.exchange.application.ports;

import com.ermiyas.exchange.domain.orderbook.BetFillAgreement;
import java.util.List;
public interface BetAgreementRepository {
    void save(BetFillAgreement agreement);
    List<BetFillAgreement> findAll();
    
}