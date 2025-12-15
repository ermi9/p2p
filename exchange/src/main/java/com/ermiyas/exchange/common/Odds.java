package com.ermiyas.exchange.common;
import java.math.BigDecimal;

public final class Odds{
    private final BigDecimal value;
    public Odds(BigDecimal value){
        if(value==null || value.compareTo(BigDecimal.ONE)<=0){
            throw new IllegalArgumentException("Odds must be greater than one");

        }
        this.value=value;
    }
    public BigDecimal value(){
        return value;
    }
    public BigDecimal profitMultiplier(){
        return value.subtract(BigDecimal.ONE);
    }

}