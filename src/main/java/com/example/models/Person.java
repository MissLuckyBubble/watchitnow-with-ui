package com.example.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="People")
public class Person extends MainModel {
    @Column(nullable = false)
    private  String name;
    @Column(nullable = false, name = "birth_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthDate;
    @Column
    private String picture;
}
