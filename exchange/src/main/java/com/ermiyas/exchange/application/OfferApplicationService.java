package com.ermiyas.exchange.application;

import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.wallet.Wallet;
import com.ermiyas.exchange.infrastructure.persistence.WalletPersistenceAdapter;
import com.ermiyas.exchange.infrastructure.persistence.entity.OffersEntity;
import com.ermiyas.exchange.infrastructure.persistence.entity.UsersEntity;
import com.ermiyas.exchange.infrastructure.persistence.entity.OutcomesEntity;
import com.ermiyas.exchange.infrastructure.persistence.jpa.OfferJpaRepository;
import com.ermiyas.exchange.infrastructure.persistence.jpa.UsersJpaRepository;
import com.ermiyas.exchange.infrastructure.persistence.jpa.OutcomeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OfferApplicationService {

    private final WalletPersistenceAdapter walletAdapter;
    private final OfferJpaRepository offersRepository;
    private final UsersJpaRepository userRepository;
    private final OutcomeJpaRepository outcomesRepository;

    /**
     * Executes the full betting offer flow: 
     * 1. Validates funds 2. Reserves money 3. Creates the market offer.
     */
    @Transactional
    public void createNewOffer(Long userId, Long outcomeId, BigDecimal stakeVal, BigDecimal oddsVal) {
        // 1. Prepare domain value objects for math
        Money stake = new Money(stakeVal);
        Odds odds = new Odds(oddsVal);
        
        // 2. Load the Wallet Aggregate and Reserve Funds
        // This will throw InsufficientFundsException if the user is broke
        Wallet wallet = walletAdapter.loadWalletByUserId(userId);
        wallet.reserve(stake); 
        
        // 3. Instantiate the Offer Aggregate to set initial business state
        Offer domainOffer = Offer.create(stake, odds);
        
        // 4. Persist the updated Wallet (Available balance drops, Reserved rises)
        walletAdapter.saveWallet(userId, wallet);
        
        // 5. Save the Offer to the database
        UsersEntity user = userRepository.getReferenceById(userId);
        OutcomesEntity outcome = outcomesRepository.getReferenceById(outcomeId);

        OffersEntity entity = OffersEntity.builder()
                .user(user)
                .outcome(outcome)
                .odds(oddsVal)
                .initialStake(stakeVal)
                .remainingStake(stakeVal)
                .status(domainOffer.status().name()) // Sets to 'OPEN'
                .build();
        
        offersRepository.save(entity);
    }
}