package com.example.dto;

import com.example.models.Movie;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MovieDTO extends BaseDTO<Movie> {
    private String title;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate release_date;
    private String description;
    private String trailer;
    private String poster_url;
    @JsonIgnoreProperties("movies")
    private Set<PlatformDTO> moviePlatforms;


    public MovieDTO(Movie movie){
        super(movie);
    }


    @Override
    public BaseDTO<Movie>


    convertToDTO(Movie entity) {
        setId(entity.getId());
        setTitle(entity.getTitle());
        setDescription(entity.getDescription());
        setRelease_date(entity.getRelease_date());
        setTrailer(entity.getTrailer());
        setPoster_url(entity.getPoster_url());
        setCreatedAt(entity.getCreatedAt());
        setUpdatedAt(entity.getUpdatedAt());

        setMoviePlatforms(entity.getMoviePlatforms()
                .stream()
                .map(PlatformDTO::new)
                .collect(Collectors.toSet()));
        return this;
    }


}
