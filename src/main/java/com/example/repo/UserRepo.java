package com.example.repo;

import com.example.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

public interface UserRepo extends JpaRepository<User,Long>, JpaSpecificationExecutor<User>  {
    User findByUsername(String username);
}
