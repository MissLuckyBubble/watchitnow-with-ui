package com.example.services;

import com.example.models.Movie;
import com.example.repo.MovieRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieService extends BaseService<Movie> {
    @Autowired
    MovieRepo movieRepo;

    @Override
    protected JpaRepository<Movie,Long> getRepo() {return movieRepo;}

    public Page<Movie> list(Pageable pageable) {
        return movieRepo.findAll(pageable);
    }
}
