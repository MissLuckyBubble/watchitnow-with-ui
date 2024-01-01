package com.example.services;

import com.example.models.Cast;
import com.example.models.Movie;
import com.example.models.Person;
import com.example.repo.CastRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CastService extends  BaseService<Cast> {
    @Autowired
    CastRepo castRepo;

    @Override
    protected JpaRepository getRepo() {
        return  castRepo;
    }

    public boolean existsByMovieAndPerson(Movie movie, Person person) {
        return castRepo.existsByMovieAndPerson(movie, person);
    }
    public List<Cast> findAllByPersonId(Long personId){
        return castRepo.findAllByPersonId(personId);
    }
    public List<Cast> findAllByMovieId(Long movieId) {return castRepo.findAllByMovieId(movieId);}
}
