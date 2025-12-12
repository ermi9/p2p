package com.exchange.domain.common;
import java.math.BigDecimal;

public records Odds(BigDecimal value){
    public Odds{
        if(value==null || value.compareTo(BIgDecimal.ONE)<=0)
            throw new IllegalArgumentException("Odds must be >1");
    }
}