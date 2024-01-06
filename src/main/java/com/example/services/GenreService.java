package com.example.services;

import com.example.models.Genre;
import com.example.models.Movie;
import com.example.repo.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class GenreService extends BaseService<Genre> {

    @Autowired
    GenreRepository genreRepository;

    @Override
    protected JpaRepository<Genre, Long> getRepo() {
        return genreRepository;
    }

    public Page<Genre> list(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

}
