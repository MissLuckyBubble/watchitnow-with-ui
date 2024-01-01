package com.example.services;

import com.example.models.Person;
import com.example.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
}
