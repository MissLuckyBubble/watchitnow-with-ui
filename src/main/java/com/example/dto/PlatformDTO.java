package com.example.dto;

import com.example.models.Platform;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PlatformDTO extends BaseDTO<Platform> {

    private String title;
    private String picture;
    private String link;
    @JsonIgnoreProperties("moviePlatforms")
    private Set<MovieDTO> movies;

    public PlatformDTO(Platform platform){
        super(platform);
    }

    @Override
    public BaseDTO<Platform> convertToDTO(Platform entity) {
        setId(entity.getId());
        setTitle(entity.getTitle());
        setPicture(entity.getPicture());
        setLink(entity.getLink());
        setCreatedAt(entity.getCreatedAt());
        setUpdatedAt(entity.getUpdatedAt());
        setMovies(entity.getMovies()
                .stream()
                .map(MovieDTO::new)
                .collect(Collectors.toSet()));
        return this;
    }
}
