package com.ermiyas.exchange.domain.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * PURE OOP: The User Aggregate.
 * In a professional system, this would link to Security/Auth.
 * Here it serves as the owner of the Wallet.
 */
@Entity
@Table(name = "users")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    // The Wallet is the financial representative of the User
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;
}