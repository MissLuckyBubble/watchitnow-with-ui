package com.example.repo;

import com.example.models.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepo  extends JpaRepository<Platform,Long> {
}
