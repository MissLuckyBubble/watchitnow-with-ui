package com.example.repo;

import com.example.models.Genre;
import com.example.models.Season;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SeasonRepo extends JpaRepository<Season,Long> {

}
