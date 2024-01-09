package com.example.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Genres")
public class Genre extends MainModel {
    @Column(nullable = false)
    private String name;
    @Column()
    private String description;


}
