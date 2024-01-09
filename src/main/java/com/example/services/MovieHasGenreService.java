package com.example.services;

import com.example.models.MovieHasGenre;
import com.example.repo.MovieHasGenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieHasGenreService extends BaseService<MovieHasGenre> {
    @Autowired
    MovieHasGenreRepo hasGenreRepo;
    @Override
    protected JpaRepository getRepo() {
        return hasGenreRepo;
    }
}
