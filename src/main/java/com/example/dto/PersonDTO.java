package com.example.dto;

import com.example.models.Person;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonDTO extends  BaseDTO<Person>{
    private  String name;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthDate;
    private String picture;

    public PersonDTO(Person person){
        super(person);
    }

    @Override
    public BaseDTO<Person> convertToDTO(Person entity) {
        setId(entity.getId());

        setName(entity.getName());
        setBirthDate(entity.getBirthDate());
        setPicture(entity.getPicture());

        setCreatedAt(entity.getCreatedAt());
        setUpdatedAt(entity.getUpdatedAt());
        return this;
    }
}
