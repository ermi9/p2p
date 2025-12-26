package com.ermiyas.exchange.domain.offer;

import java.util.Objects;
import java.util.UUID;

public final class OfferId {
    private final UUID value;
    
    private OfferId(UUID value){
        this.value=value;
    }
    public static OfferId newId(){
        return new OfferId(UUID.randomUUID());
    }
    public static OfferId of(UUID value){
        return new OfferId(Objects.requireNonNull(value));
    }   
    public UUID value(){
        return value;
    }  

    @Override
    public boolean equals(Object o){
        if(this==o) return true;

        if(!(o instanceof OfferId)) return false;
        OfferId other =(OfferId) o;
        return value.equals(other.value);
    }
    @Override
    public int hashCode(){
        return value.hashCode();
    }

    @Override
    public String toString(){
        return value.toString();
    }
}
