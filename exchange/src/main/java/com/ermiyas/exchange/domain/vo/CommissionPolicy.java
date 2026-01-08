package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.ExchangeException;

/**
  OOP: Strategy Interface for Commission.
 * Allows OCP-compliant extensions like Flat fees or VIP rates.
 */
public interface CommissionPolicy {
    Money apply(Money netProfit) throws ExchangeException;
    String getPolicyDescription();
}