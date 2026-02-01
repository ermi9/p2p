package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.model.user.User; 
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/**
 * Bet Entity
 */
@Entity
@Table(name = "bets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Changed to EAGER 
     * Used @JsonIncludeProperties ,
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offer_id")
    @JsonIncludeProperties({"id", "predictedOutcome", "event", "odds"})
    private Offer offer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taker_id")
    @JsonIncludeProperties({"id", "username"})
    private User taker; 

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "maker_stake", nullable = false))
    private Money makerStake;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "taker_liability", nullable = false))
    private Money takerLiability;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "odds", nullable = false))
    private Odds odds;

    private String reference;

    @Enumerated(EnumType.STRING)
    private BetStatus status;

    // 

    public void resolve(Outcome eventResult, CommissionPolicy policy) throws ExchangeException {
        if(eventResult == offer.getPredictedOutcome())
            handleMakerWin(policy);
        else{
            handleTakerWin(policy);
        }
        this.status=BetStatus.SETTLED;
    
    }
    private void handleMakerWin(CommissionPolicy policy) throws ExchangeException{
        Wallet makerWallet=getMaker().getWallet();
        Wallet takerWallet=taker.getWallet();

        makerWallet.settleWin( makerStake,takerLiability, policy);
        takerWallet.settleLoss(takerLiability);

    }



    private void handleTakerWin(CommissionPolicy policy) throws ExchangeException{
        Wallet makerWallet=getMaker().getWallet();
        Wallet takerWallet=taker.getWallet();

        makerWallet.settleLoss(makerStake);
        takerWallet.settleWin(takerLiability, makerStake, policy);


    }

    public User getMaker() { 
        return offer.getMaker(); 
    }
}