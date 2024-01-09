package com.example.repo;

import com.example.models.MovieHasGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieHasGenreRepo extends JpaRepository<MovieHasGenre, Long> {
}
