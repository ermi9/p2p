package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.model.user.UserFactory;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Password;
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

        if (user instanceof StandardUser) {
            StandardUser standardUser = (StandardUser) user;
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

    @Transactional
    public void updatePassword(Long userId, String newRawPassword) throws ExchangeException {
        User user = getUserById(userId);
        // Promotes raw text to a secure, validated Password VO
        Password securePassword = Password.create(newRawPassword);
        // Updates the entity using the interface contract
        user.updatePassword(securePassword);
        userRepository.save(user);
    }

    public User login(String username, String password) throws ExchangeException {
        //  Find user via Optional check
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (!userOpt.isPresent()) {
            throw new UserNotFoundException("Invalid username or password.");
        }

        User user = userOpt.get();

        // Verify credentials using the Entity's authenticate method
        if (!user.authenticate(password)) {
            throw new UserNotFoundException("Invalid username or password.");
        }

        return user;
    }
}