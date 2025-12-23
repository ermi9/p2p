package com.ermiyas.exchange.referenceOdds.domain;
/*
 *Market types suppported by the referenceOdds system 
 * 
 * New market types can be added without changing
 * existing providers or usecases
 *
 */
public enum MarketType {
    H2H,    //Match winner (Home / Draw / Away)
    TOTALS, //Over / Under
    HANDICAP
    
}
