package com.example.rest;

import com.example.dto.BaseDTO;
import com.example.dto.MovieDTO;
import com.example.dto.PersonDTO;
import com.example.models.Cast;
import com.example.models.Movie;
import com.example.models.Person;
import com.example.services.CastService;
import com.example.services.MovieService;
import com.example.services.PersonService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/people")
@RestController
public class PersonController {

    private PersonService personService;
    private CastService castService;
    private MovieService movieService;
    private final ModelMapper modelMapper;

    @Autowired
    private PersonController(PersonService personService, CastService castService, MovieService movieService, ModelMapper modelMapper){
        this.personService = personService;
        this.movieService = movieService;
        this.modelMapper = modelMapper;
        this.castService = castService;
    }

    @GetMapping()
    public List<PersonDTO> getPeople(){
        List<Person> people = personService.findAll();
        return people
                .stream()
                .map(this::convertToPersonDTO)
                .collect(Collectors.toList());

    }

    private PersonDTO convertToPersonDTO(Person person) {
        final PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
        return personDTO;
    }

    @GetMapping("/{personId}")
    public PersonDTO getPerson(@PathVariable(name = "personId") long personId){
        Optional<Person> optionalPerson = personService.getEntity(personId);
        return optionalPerson.map(this::convertToPersonDTO).orElse(null);
    }

    @GetMapping("/{personId}/movies")
    public List<MovieDTO> getMovies(@PathVariable(name = "personId") long personId){
        Optional<Person> optionalPerson = personService.getEntity(personId);
        List<MovieDTO> movieDTOS = new ArrayList<>();
        if(!optionalPerson.isPresent()){
            return movieDTOS;
        }
        List<Cast> casts = castService.findAllByPersonId(personId);
        movieDTOS = casts.stream()
                .map(cast -> {
                    Optional<Movie> optionalMovie = movieService.getEntity(cast.getMovie().getId());
                    if(optionalMovie.isPresent()){
                    return convertToMovieDTO(optionalMovie.get());}
                    else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return movieDTOS;
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        final MovieDTO result = modelMapper.map(movie, MovieDTO.class);
        return result;
    }
    @PostMapping()
    public BaseDTO<Person> create(@RequestBody PersonDTO newPerson){
        Person person = converPersonDTOtoModel(newPerson);
        return convertToPersonDTO(personService.create(person));
    }

    private Person converPersonDTOtoModel(PersonDTO personDTO) {
        Person person = modelMapper.map(personDTO, Person.class);
        return person;
    }

    @PutMapping()
    public BaseDTO<Person> update(@RequestBody PersonDTO updatedPerson){
        Person person = converPersonDTOtoModel(updatedPerson);
        return convertToPersonDTO(personService.update(person));
    }

    @DeleteMapping("/{personId}")
    public ResponseEntity<String> delete(@PathVariable long personId){
        boolean isRemoved = personService.remove(personId);
        String deletedMessage = "Person with id: " + personId + " was deleted";
        String notDeletedMessage = "Person with id: " + personId + " does not exist";
        return isRemoved? new ResponseEntity(deletedMessage, HttpStatusCode.valueOf(200)) :
                new ResponseEntity<>(notDeletedMessage,HttpStatusCode.valueOf(404));
    }

}
