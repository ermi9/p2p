package com.ermiyas.exchange.domain.repository.wallet;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.infrastructure.persistence.JpaWalletRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WalletRepositoryImpl implements WalletRepository {
    private final JpaWalletRepository jpaWalletRepository;

    @Override public Wallet save(Wallet wallet) { return jpaWalletRepository.save(wallet); }
    @Override public Optional<Wallet> findById(Long id) { return jpaWalletRepository.findById(id); }
    @Override public List<Wallet> findAll() { return jpaWalletRepository.findAll(); }

    @Override
    public Optional<Wallet> getByUserId(Long userId) {
        return jpaWalletRepository.findByUserId(userId);
    }
    @Override
    public Optional<Wallet> getByUserIdWithLock(Long userId){
        return jpaWalletRepository.findByUserIdWithLock(userId);
    }
    @Override
    public long count(){
        return jpaWalletRepository.count();
    }
    @Override
    public void deleteById(Long id){
         jpaWalletRepository.deleteById(id);
    }
}