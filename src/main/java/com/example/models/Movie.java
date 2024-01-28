package com.example.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "Movies")
public class Movie extends MainModel{
    @Column(nullable = false,length = 255)
    @NotEmpty(message = "Title is required.")
    private String title;
    @Column(nullable = false)
    @NotNull(message = "Release Date is required.")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate release_date;
    @Column(length = 255)
    private String trailer;
    @Column(nullable = false )
    private String description;
    @Column(nullable = false)
    private String poster_url;
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "movie_is_on_platform",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "platform_id")
    )
    private Set<Platform> moviePlatforms = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Cast> movieCast = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<MovieHasGenre> movieGenres = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRatesMovie> movieRatings = new HashSet<>();

    @Column()
    private float rate = 0;

    public Movie(){
        this.moviePlatforms = new HashSet<>();
        this.movieCast = new HashSet<>();
        this.movieGenres = new HashSet<>();
        this.movieRatings = new HashSet<>();
        this.rate = 0;
    }

    public int getRate() {
        int totalRate = 0;
        for (UserRatesMovie mR : movieRatings) {
            totalRate+= mR.getRating();
        }

        if (!movieRatings.isEmpty()) {
            return totalRate / movieRatings.size();
        } else {
            return 0; // Default rate when there are no ratings
        }
    }
}
