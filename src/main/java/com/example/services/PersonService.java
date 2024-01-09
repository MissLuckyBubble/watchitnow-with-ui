package com.example.services;

import com.example.models.Movie;
import com.example.models.Person;
import com.example.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends BaseService<Person> {
    @Autowired
    PersonRepo personRepo;
    @Override
    protected JpaRepository<Person, Long> getRepo() {
        return personRepo;
    }
    public Page<Person> list(Pageable pageable) {
        return personRepo.findAll(pageable);
    }
}
