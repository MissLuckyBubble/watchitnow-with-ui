package com.example.models;

import com.example.data.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Set;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Component
@Entity
@Table(name = "Users")
public class User extends MainModel {

    @Column(nullable = false, length = 255)
    @NotNull(message = "Username is required.")
    private String username;
    @Column(nullable = false, length = 255)
    @NotNull(message = "Password is required.")
    private String password;
    @Column(nullable = false, length = 255)
    @NotNull(message = "Email is required.")
    @Email(message = "Enter a valid email.")
    private String email;
    @Column(name = "last_logged_at")
    private LocalDateTime lastLoggedAt;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
