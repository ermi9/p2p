package com.ermiyas.exchange.common;
import java.math.BigDecimal;


import java.util.Objects;
public final class Odds{
    private final BigDecimal value;
    
    public Odds(BigDecimal value){
    this.value=Objects.requireNonNull(value,"value");
    if(value.compareTo(BigDecimal.ONE)<=0)
        throw new IllegalArgumentException("Odds must be greater than 1");    
    }
    public BigDecimal value(){
        return value;
    }
    public BigDecimal minusOne(){
        return value.subtract(BigDecimal.ONE);
    }


}