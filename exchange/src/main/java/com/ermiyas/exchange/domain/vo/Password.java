package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import org.springframework.security.crypto.bcrypt.BCrypt;


public final class Password {
    private final String hashedValue; 

    private Password(String plainText) throws ExchangeException {
        validate(plainText);
        this.hashedValue = BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public static Password create(String plainText) throws ExchangeException {
        return new Password(plainText);
    }


    public boolean matches(String candidateRaw) {
        return BCrypt.checkpw(candidateRaw, this.hashedValue);
    }

    private void validate(String text) throws ExchangeException {
        if (text == null || text.length() < 8) {
            throw new IllegalBetException("Security Violation: Password must be at least 8 characters.");
        }
    }

    @Override
    public String toString() {
        return "********"; 
    }
}