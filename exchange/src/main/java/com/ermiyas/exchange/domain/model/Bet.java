package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.model.user.User; 
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

/**
 * Bet Entity.
 * Represents a matched contract between two users.
 * Updated to support dynamic UI rendering without recursion.
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
     * Changed to EAGER fetch to ensure match data is available for the UI summary.
     * Use @JsonIncludeProperties to send only the data needed for the dashboard cards,
     * which prevents infinite recursion with the Event and User entities.
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
        if (eventResult == Outcome.DRAW) {
            handleDraw();
        } else if (eventResult == offer.getPredictedOutcome()) {
            handleMakerWin(policy);
        } else {
            handleTakerWin(policy);
        }
        this.status = BetStatus.SETTLED;
    }

    private void handleDraw() throws ExchangeException {
        if (getMaker() instanceof StandardUser maker && taker instanceof StandardUser standardTaker) {
            maker.getWallet().unreserve(makerStake);
            standardTaker.getWallet().unreserve(takerLiability);
        }
    }

    private void handleMakerWin(CommissionPolicy policy) throws ExchangeException {
        if (getMaker() instanceof StandardUser maker && taker instanceof StandardUser standardTaker) {
            maker.getWallet().settleWin(makerStake, takerLiability, policy);
            standardTaker.getWallet().settleLoss(takerLiability);
        }
    }

    private void handleTakerWin(CommissionPolicy policy) throws ExchangeException {
        if (getMaker() instanceof StandardUser maker && taker instanceof StandardUser standardTaker) {
            standardTaker.getWallet().settleWin(takerLiability, makerStake, policy);
            maker.getWallet().settleLoss(makerStake);
        } else {
            throw new IllegalBetException("System Integrity Error: Non-player account found in settlement.");
        }
    }

    public User getMaker() { 
        return offer.getMaker(); 
    }
}