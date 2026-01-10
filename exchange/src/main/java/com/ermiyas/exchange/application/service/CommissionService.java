package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import org.springframework.stereotype.Service;


@Service
public class CommissionService {

    /**
     * Calculates what the user actually takes home.
     */
    public Money estimateNetProfit(Money grossProfit, CommissionPolicy policy) throws ExchangeException {
        return policy.apply(grossProfit);
    }

    /**
     * Calculates the house cut (Gross Profit - Net Profit).
     */
    public Money estimateCommission(Money grossProfit, CommissionPolicy policy) throws ExchangeException {
        Money netProfit = policy.apply(grossProfit);
        return grossProfit.minus(netProfit);
    }
}