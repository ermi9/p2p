package com.ermiyas.exchange.common;
// Package declaration corrected so the class can be resolved by existing imports.
import java.math.BigDecimal;
import java.util.Objects;
public final class Money{
    private final BigDecimal value;

    public Money(BigDecimal value){
        this.value=Objects.requireNonNull(value,"value");
        if(value.signum()<0) throw new IllegalArgumentException("Money must be positive");
    }

    //addition and subtraction
    public Money plus(Money other){
        Objects.requireNonNull(other);
        return new Money(this.value.add(other.value));
    }
    public Money minus(Money other){
        Objects.requireNonNull(other);
        BigDecimal result=this.value.subtract(other.value);
        if(result.signum()<0)
            throw new IllegalArgumentException("Resulting Money cannot be Negative");
        return new Money(result);
    }

    //in some codes i used the getter method for calculations, will check and refactor
    public BigDecimal value(){
        return value;
    }
    //multiplier logic
    public Money multiply(BigDecimal factor){
        Objects.requireNonNull(factor);
        if(factor.signum()<0)
            throw new IllegalArgumentException("MUltipler cannot be negative");
        return new Money(this.value.multiply(factor));
    }
//comparision logic
    public int compareTo(Money other){
        Objects.requireNonNull(other);
        return this.value.compareTo(other.value);
    }
}
