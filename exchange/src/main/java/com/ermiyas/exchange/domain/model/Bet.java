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

import java.time.Instant;

/**
 *
 * This class represents a binding contract between a Maker and a Taker.
 * It encapsulates the financial settlement logic, ensuring that only users 
 * with financial capabilities (StandardUsers) can participate in fund movements.
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taker_id")
    private User taker; 

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "maker_stake", nullable = false))
    @Setter(AccessLevel.NONE) 
    private Money makerStake;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "taker_liability", nullable = false))
    @Setter(AccessLevel.NONE) 
    private Money takerLiability;

    @Embedded
    private Odds odds;

    private String reference;
    
    @Column(updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private BetStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = BetStatus.MATCHED;
        }
    }


    /**
     * PURE OOP: Self-Settlement Orchestration.
     * * Logic: Compares the actual event result with the predicted outcome from the offer.
     * Delegates fund movement to specific win/loss handlers.
     * * @param actualResult The final result of the fixture.
     * @param policy The commission structure to apply to the winner's profit.
     * @throws ExchangeException if the bet is already settled or participants are invalid.
     */
    public void resolve(Outcome actualResult, CommissionPolicy policy) throws ExchangeException {
        if (this.status == BetStatus.SETTLED) {
            throw new IllegalBetException("Integrity Error: Bet #" + id + " is already settled.");
        }

        Outcome predicted = offer.getPredictedOutcome();
        
        // Determination logic: Maker wins if prediction matches the result
        if (actualResult == predicted) {
            handleMakerWin(policy);
        } else {
            handleTakerWin(policy);
        }

        this.status = BetStatus.SETTLED;
    }

    /**
     * Logic: Payout process when the Maker wins.
     * * Verification: Ensures both participants are StandardUsers. 
     * Admins do not have wallets and cannot participate in settlements.
     */
    private void handleMakerWin(CommissionPolicy policy) throws ExchangeException {
        User maker = this.getMaker();
        
        // PURE OOP: Use 'instanceof' pattern matching for safe type handling
        if (maker instanceof StandardUser standardMaker && taker instanceof StandardUser standardTaker) {

            standardMaker.getWallet().settleWin(makerStake, takerLiability, policy);
            
            // Taker loses: Reserved liability is deducted from total balance
            standardTaker.getWallet().settleLoss(takerLiability);
        } else {
            throw new IllegalBetException("System Integrity Error: Non-player account found in financial settlement.");
        }
    }

    /**
     * Logic: Payout process when the Taker wins.
     */
    private void handleTakerWin(CommissionPolicy policy) throws ExchangeException {
        User maker = this.getMaker();

        if (maker instanceof StandardUser standardMaker && taker instanceof StandardUser standardTaker) {
            // Taker receives: Their reserved liability (unreserved) + Maker's stake (profit)
            standardTaker.getWallet().settleWin(takerLiability, makerStake, policy);
            
            // Maker loses: Their reserved stake is deducted from total balance
            standardMaker.getWallet().settleLoss(makerStake);
        } else {
            throw new IllegalBetException("System Integrity Error: Non-player account found in financial settlement.");
        }
    }

    /**
     * Accesses the Maker through the associated Offer.
     */
    public User getMaker() {
        return offer.getMaker();
    }
}