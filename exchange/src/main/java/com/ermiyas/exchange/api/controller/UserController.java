package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.api.dto.ExchangeDtos.RegisterRequest;
import com.ermiyas.exchange.application.service.UserService;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://127.0.0.1:5501")
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ExchangeDtos.LoginRequest request) throws ExchangeException {
        // Authenticate via the Service logic
        User user = userService.login(request.getUsername(), request.getPassword());
        
        // Manual role mapping based on Domain Entity role names
        String role;
        if (user.getRoleName().equals("EXCHANGE_ADMIN")) {
            role = "ADMIN";
        } else {
            role = "STANDARD";
        }

        // Build response Map manually
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) throws ExchangeException {
        // Extract new raw password from the JSON body
        String newPassword = request.get("password");
        userService.updatePassword(id, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
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