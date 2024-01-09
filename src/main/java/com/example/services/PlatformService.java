package com.example.services;

import com.example.models.Genre;
import com.example.models.Platform;
import com.example.repo.PlatformRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PlatformService extends BaseService<Platform>{
    @Autowired
    PlatformRepo platformRepo;

    @Override
    protected JpaRepository<Platform, Long> getRepo() {
        return platformRepo;
    }
    public Page<Platform> list(Pageable pageable) {
        return platformRepo.findAll(pageable);
    }

}