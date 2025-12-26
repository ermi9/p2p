package com.ermiyas.exchange.domain.orderbook;
import com.ermiyas.exchange.domain.offer.OfferId;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;

import java.time.Instant;
import java.util.Objects;

public class BetFillAgreement {
    private final OfferId offerId;
    private final long takerUserId;

    //new field added
    private final Money makerStakePortion;
    private final Money liability;
    private final Odds odds;

    private final Instant createdAt;
    private final String reference;

    private  BetFillAgreement(OfferId offerId,long takerUserId,Money makerStakePortion,Money liability,Odds odds, String reference,Instant createdAt){
        this.offerId = offerId;
        this.takerUserId = takerUserId;
        this.makerStakePortion = makerStakePortion; // ← assignment for new field
        this.liability = liability;
        this.odds = odds;                           // ← assignment for new field
        this.reference = reference;
        this.createdAt = createdAt;
    }
    public static BetFillAgreement of(OfferId offerId,long takerUserId,Money makerStakePortion,Money liability,Odds odds,String reference){
        Objects.requireNonNull(offerId);
        Objects.requireNonNull(liability);
        Objects.requireNonNull(odds,"odds");
        Objects.requireNonNull(reference,"reference");

        if(takerUserId<=0) throw new IllegalArgumentException("Invalid taker user id");

        return new BetFillAgreement(offerId, takerUserId,makerStakePortion ,liability,odds, reference,Instant.now());
    }

    //getters
    public OfferId offerId() { return offerId; }
    public long takerUserId() { return takerUserId; }
    public Money makerStakePortion(){return makerStakePortion;}
    public Money liability() { return liability; }
    public Odds odds(){return odds;}
    public Instant createdAt() { return createdAt; }
    public String reference() { return reference; }

        
}
