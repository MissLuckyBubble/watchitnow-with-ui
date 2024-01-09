package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name="Platforms")
public class Platform extends MainModel{
    @Column
    private String title;
    @Column
    private String picture;
    @Column
    private String link;

    @JsonIgnore
    @ManyToMany(mappedBy = "moviePlatforms", fetch = FetchType.EAGER)
    private Set<Movie> movies = new HashSet<>();
}
