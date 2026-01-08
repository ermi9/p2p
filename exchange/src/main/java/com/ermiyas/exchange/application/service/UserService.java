package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.model.user.UserFactory;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IdentityConflictException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;


    @Transactional(rollbackFor = Exception.class)
    public User registerStandardUser(String username, String email, String password) throws ExchangeException {

        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IdentityConflictException("Registration Failed: Username '" + username + "' is already taken.");
        }

        // We cast to User because the repository expects the Entity type
        User user = (User) UserFactory.createStandard(username, email, password);

        if (user instanceof StandardUser standardUser) {
            Wallet wallet = new Wallet(standardUser, Money.zero());
            standardUser.setWallet(wallet);
            walletRepository.save(wallet);
        }

        return userRepository.save(user);
    }

    public User getUserById(Long id) throws ExchangeException {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (!userOpt.isPresent()) {
            throw new UserNotFoundException("Lookup Failed: No user found with ID: " + id);
        }
        
        return userOpt.get();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}