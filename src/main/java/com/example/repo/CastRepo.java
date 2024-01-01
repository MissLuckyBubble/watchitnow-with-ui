package com.example.repo;

import com.example.models.Cast;
import com.example.models.Movie;
import com.example.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CastRepo extends JpaRepository<Cast,Long> {
    boolean existsByMovieAndPerson(Movie movie, Person person);
    List<Cast> findAllByPersonId(Long PersonId);
    List<Cast> findAllByMovieId(Long MovieId);
}
