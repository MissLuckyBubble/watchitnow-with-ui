package com.example.services;

import com.example.models.Genre;
import com.example.models.Movie;
import com.example.models.Season;
import com.example.repo.SeasonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SeasonService  extends BaseService<Season> {
    @Autowired
    SeasonRepo seasonRepo;

    public Page<Season> list(Pageable pageable) {
        return seasonRepo.findAll(pageable);
    }

    @Override
    protected JpaRepository<Season, Long> getRepo() {
       return seasonRepo;
    }
}
