package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.exception.ExchangeException;

public interface UserInterface {
    String getUsername();
    String getRoleName();
    void validateTransaction(Money amount) throws ExchangeException;
    boolean authenticate(String password);
}