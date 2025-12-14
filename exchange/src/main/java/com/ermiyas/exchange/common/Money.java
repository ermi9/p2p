package com.ermiyas.exchange.domain.common;
import java.math.BigDecimal;
import java

public record Money(BigDecimal value){
    //constructor
    public Money{
        if(value==null || value.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException("Money must be >=0");

    }
    //deduction logic
    public Money minus(Money other){
        BigDecimal r=value.subtract(other.value);
        if(r.compareTo(BigDecimal.ZERO)<0) throw new IllegalStateException("Negative Money");
        return new Money(r);

    }
    //deposit logic
public Money plus(Money other){
    return new Money(value.add(other.value));
}

}


