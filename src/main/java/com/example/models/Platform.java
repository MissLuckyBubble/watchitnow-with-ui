package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
    @ManyToMany(mappedBy = "moviePlatforms")
    private Set<Movie> movies = new HashSet<>();
}
