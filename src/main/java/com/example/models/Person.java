package com.example.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
}
