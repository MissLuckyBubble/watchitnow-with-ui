package com.example.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.data.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Component
@Table(name = "Users")
public class User extends MainModel {

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Username is required.")
    private String username;
    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Password is required.")
    private String password;
    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Email is required.")
    @Email(message = "Enter a valid email.")
    private String email;
    @Column(name = "last_logged_at")
    private LocalDateTime lastLoggedAt;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserRatesMovie> ratedMovies = new HashSet<>();
}
