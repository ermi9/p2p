package com.ermiyas.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor @Builder
public class UsersEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="username",nullable = false,unique = true)
    private String username;

    @Column(name="email",unique=true,nullable = false)
    private String email;

    @Column(name="created_at",nullable=false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }

}
