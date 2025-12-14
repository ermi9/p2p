package com.ermiyas.exchange.domain.common;
import java.math.BigDecimal;

public final class Money{
    private final BigDecimal value;
    public Money(BigDecimal value){
        if(value==null || value.compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException("Money must be positive");
        this.value=value;
    }
    public Money plus(Money other){
        return new Money(this.value.add(other.value));
    }
    public Money minus(Money other){
        BigDecimal result=this.value.subtract(other.value);
        if(result.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException("Resulting Money cannot be Negative");
        return new Money(result);
    }
    public BigDecimal value(){
        return value;
    }
}