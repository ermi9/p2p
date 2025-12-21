package com.ermiyas.exchange.application.offer;

import com.ermiyas.exchange.application.ports.OfferRepository;
import com.ermiyas.exchange.common.Money;
import com.ermiyas.exchange.common.Odds;
import com.ermiyas.exchange.domain.offer.Offer;
import com.ermiyas.exchange.domain.wallet.WalletService;
import java.util.Objects;

public class CreateOfferUseCase {
    private final OfferRepository offerRepository;
    private final WalletService walletService;

    public CreateOfferUseCase(
        OfferRepository offerRepository,WalletService walletService){
            this.offerRepository=offerRepository;
            this.walletService=Objects.requireNonNull(walletService);     
           }
   
   
   
   
    public long execute(long makerUserId,long outcomeId,Odds odds,Money stake){
        //check and lock maker's money first
        walletService.reserve(makerUserId, stake);
        Offer newOffer=new Offer(0L, makerUserId,outcomeId,odds,stake);
        Offer savedOffer=offerRepository.save(newOffer);
        return savedOffer.id();


    }
    
}
