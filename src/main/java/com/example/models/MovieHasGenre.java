package com.example.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="Movie_Has_Genres")
public class MovieHasGenre extends MainModel {

    @ManyToOne
    @JoinColumn(name="movie_id")
    private Movie movie;
    @ManyToOne
    @JoinColumn(name="genre_id")
    private Genre genre;

    public MovieHasGenre(Movie movie, Genre genre) {
        super();
        this.movie = movie;
        this.genre = genre;
    }
}
