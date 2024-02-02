package com.example.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="Seasons")
public class Season  extends MainModel{

    @Min(value = 1, message = "Season Number must be greater than or equal to 1.")
    @Column(name = "season_number")
    private Integer seasonNumber;

    @Min(value = 1, message = "Number Of Episodes must be greater than or equal to 1.")
    @Column(name = "number_of_episodes")
    private Integer numberOfEpisodes;

    @Column(name = "first_episode_release")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate firstEpisodeRelease;

    @Column(name = "last_episode_release")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate lastEpisodeRelease;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;


}
