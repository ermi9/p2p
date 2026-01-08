package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.vo.Password;
import com.ermiyas.exchange.domain.exception.ExchangeException;


public class UserFactory {


    public static UserInterface createStandard(String username, String email, String rawPassword) throws ExchangeException {
        Password securePassword = Password.create(rawPassword);
        
        return new StandardUser(username, email, securePassword);
    }


    public static UserInterface createAdmin(String username, String email, String rawPassword) throws ExchangeException {
        Password securePassword = Password.create(rawPassword);
        
        return new AdminUser(username, email, securePassword);
    }
}