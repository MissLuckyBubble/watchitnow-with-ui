package com.example.services;

import com.example.models.MainModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public abstract class BaseService <U extends MainModel> {

    protected abstract JpaRepository<U,Long> getRepo();

    public List<U> findAll(){
      return getRepo().findAll();
    }

    public Optional<U> getEntity(Long id){
        return getRepo().findById(id);
    }

    public U create(U entity){
        entity.setCreatedAt(LocalDateTime.now());
        return getRepo().save(entity);
    }

    public U update(U entity){
        long id = entity.getId();
        Optional<U> optionalEntity = getRepo().findById(id);
        if(optionalEntity.isPresent()){
            entity.setCreatedAt(optionalEntity.get().getCreatedAt());
            entity.setUpdatedAt(LocalDateTime.now());
            return getRepo().save(entity);
        }
        return null;
    }


    @Transactional
    public boolean remove(long id) {
        Optional<U> optionalEntity = getRepo().findById(id);
        if(optionalEntity.isPresent()){
            getRepo().delete(optionalEntity.get());
            return true;
        }
        return false;
    }

}

