package com.example.repo;

import com.example.models.Movie;
import com.example.models.User;
import com.example.models.UserRatesMovie;
import org.hibernate.mapping.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRatesRepo extends JpaRepository<UserRatesMovie,Long> {
    boolean existsByMovieAndUser(Movie movie, User user);
    UserRatesMovie findUserRatesMovieByMovieAndUser(Movie movie, User user);
    List<UserRatesMovie> findAllByMovie(Movie movie);
}
