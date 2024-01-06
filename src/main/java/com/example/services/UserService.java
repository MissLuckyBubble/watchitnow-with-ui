package com.example.services;


import com.example.models.User;
import com.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User> {
    @Autowired
    UserRepo userRepo;

    @Override
    protected JpaRepository<User, Long> getRepo() {
        return userRepo;
    }


}
