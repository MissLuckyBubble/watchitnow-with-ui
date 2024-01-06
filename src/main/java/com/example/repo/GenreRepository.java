package com.example.repo;

import com.example.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  GenreRepository extends JpaRepository<Genre,Long> {
}
