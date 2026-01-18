package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos.RegisterRequest;
import com.ermiyas.exchange.application.service.UserService;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://127.0.0.1:5500")

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) throws ExchangeException {
        User user = userService.registerStandardUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok("User created with ID: " + user.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws ExchangeException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}