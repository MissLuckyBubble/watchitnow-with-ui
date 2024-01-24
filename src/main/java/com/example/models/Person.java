package com.example.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name="People")
public class Person extends MainModel {
    @Column(nullable = false)
    @NotEmpty(message = "Name is required.")
    private  String name;
    @Column(nullable = false, name="last_name")
    @NotEmpty(message = "Last name is required.")
    private  String lastName;
    @NotNull(message = "Birth Date is required.")
    @Column(nullable = false, name = "birth_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthDate;
    @Column
    private String picture;

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private Set<Cast> casts = new HashSet<>();
}
