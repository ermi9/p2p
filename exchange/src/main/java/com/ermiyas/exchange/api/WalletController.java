package com.ermiyas.exchange.api;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletRepository walletRepository;

  
    @GetMapping("/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        Wallet wallet = walletRepository.getById(userId);
        
        if (wallet == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
            "totalBalance", wallet.getTotalBalance().value(),
            "availableBalance", wallet.availableBalance().value(),
            "reservedBalance", wallet.getReservedBalance().value()
        ));
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long userId, @RequestParam double amount) {
        try {
            Wallet wallet = walletRepository.getByUserId(userId);

            Money depositAmount = new Money(BigDecimal.valueOf(amount));
            
            // For a simple deposit, we bypass commission (0%)
            CommissionPolicy noFee = new CommissionPolicy(BigDecimal.ZERO);
            
            // Using the settleWin method as a way to credit the account
            wallet.settleWin(Money.zero(), depositAmount, noFee);

            walletRepository.save(wallet);
            return ResponseEntity.ok("Deposit successful. New Balance: " + wallet.getTotalBalance());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}