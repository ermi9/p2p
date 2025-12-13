package com.ermiyas.exchange.domain.common.odds;
import java.math.BigDecimal;

public record Odds(BigDecimal value){
    public Odds{
        if(value==null || value.compareTo(BigDecimal.ONE)<=0) {
            throw new IllegalArgumentException("Odds must be greater than 1");
        }
    }
}

