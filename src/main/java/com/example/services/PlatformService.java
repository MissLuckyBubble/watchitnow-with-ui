package com.example.services;

import com.example.models.Platform;
import com.example.repo.PlatformRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

}