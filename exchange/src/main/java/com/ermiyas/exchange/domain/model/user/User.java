package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.vo.Password;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_role")
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User implements UserInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @Embedded
    private Password password;

    protected User(String username, String email, Password password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean authenticate(String rawInput) {
        return this.password.matches(rawInput);
    }
    @Override
    public void updatePassword(Password newPassword) {
        this.password = newPassword;
    }

    public abstract String getRoleName();
}