package com.example.services;

import com.example.models.Movie;
import com.example.models.User;
import com.example.models.UserRatesMovie;
import com.example.repo.UserRatesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRatesService extends BaseService {
    @Autowired
    UserRatesRepo userRatesRepo;

    @Override
    protected JpaRepository getRepo() {
        return userRatesRepo;
    }

    public boolean existsByMovieAndUser(Movie movie, User user) {
        return userRatesRepo.existsByMovieAndUser(movie, user);
    }

    public UserRatesMovie findByMovieAndUser(Movie movie, User user){
        return userRatesRepo.findUserRatesMovieByMovieAndUser(movie, user);
    }

    public List<UserRatesMovie> findAllByMovie(Movie movie){
        return userRatesRepo.findAllByMovie(movie);
    }
}
