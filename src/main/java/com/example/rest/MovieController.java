package com.example.rest;

import com.example.dto.BaseDTO;
import com.example.dto.CastDTO;
import com.example.dto.MovieDTO;
import com.example.models.Cast;
import com.example.models.Movie;
import com.example.models.Person;
import com.example.models.Platform;
import com.example.services.CastService;
import com.example.services.MovieService;
import com.example.services.PersonService;
import com.example.services.PlatformService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api-movie")
@RestController
public class MovieController {

    private MovieService movieService;
    private PlatformService platformService;
    private CastService castService;
    private PersonService personService;

    private final ModelMapper modelMapper;

    @Autowired
    private MovieController(PersonService personService, MovieService movieService, PlatformService platformService, CastService castService, ModelMapper modelMapper) {

        this.movieService = movieService;
        this.platformService = platformService;
        this.castService = castService;
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<MovieDTO> list() {
        List<Movie> movies = movieService.findAll();
        return movies
                .stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        final MovieDTO result = modelMapper.map(movie, MovieDTO.class);
        return result;
    }

    @GetMapping("/{movieId}")
    public MovieDTO getMovie(@PathVariable(name = "movieId") long movieId) {
        Optional<Movie> optionalMovie = movieService.getEntity(movieId);
        return optionalMovie.map(this::convertToMovieDTO).orElse(null);
    }

    @PostMapping()
    public MovieDTO create(@RequestBody MovieDTO newMovie) {
        Movie movie = convertMovieDTOtoModel(newMovie);
        return convertToMovieDTO(movieService.create(movie));
    }

    private Movie convertMovieDTOtoModel(MovieDTO movieDTO) {
        Movie movie = modelMapper.map(movieDTO, Movie.class);
        return movie;
    }

    @PutMapping()
    public BaseDTO<Movie> update(@RequestBody MovieDTO updatedMovie) {
        Movie movie = convertMovieDTOtoModel(updatedMovie);
        return convertToMovieDTO(movieService.update(movie));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<String> delete(@PathVariable long movieId) {
        boolean isRemoved = movieService.remove(movieId);
        String deletedMessage = "Movie with id: " + movieId + " was deleted";
        String notDeletedMessage = "Movie with id: " + movieId + " does not exist";
        return isRemoved ? new ResponseEntity(deletedMessage, HttpStatusCode.valueOf(200)) :
                new ResponseEntity<>(notDeletedMessage, HttpStatusCode.valueOf(404));
    }

    @PutMapping("/{movieId}/platforms/{platformId}")
    public ResponseEntity<String> addMovieToPlatform(@PathVariable long movieId, @PathVariable long platformId) {
        Optional<Movie> optionalMovie = movieService.getEntity(movieId);
        Optional<Platform> optionalPlatform = platformService.getEntity(platformId);

        if (optionalMovie.isPresent() && optionalPlatform.isPresent()) {

            Movie movie = optionalMovie.get();
            Platform platform = optionalPlatform.get();

            if (movie.getMoviePlatforms().contains(platform)) {
                return new ResponseEntity<>("Movie already is added to Platform.", HttpStatusCode.valueOf(200));
            } else {
                movie.getMoviePlatforms().add(platform);
                platform.getMovies().add(movie);

                movieService.update(movie);
                platformService.update(platform);
                return new ResponseEntity<>("Movie added to Platform.", HttpStatusCode.valueOf(200));
            }
        } else {
            return new ResponseEntity<>("Movie or Platform not found.", HttpStatusCode.valueOf(404));
        }
    }

    @DeleteMapping("/{movieId}/platforms/{platformId}")
    public ResponseEntity<String> deleteMovieFromPlatform(@PathVariable long movieId, @PathVariable long platformId) {
        Optional<Movie> optionalMovie = movieService.getEntity(movieId);
        Optional<Platform> optionalPlatform = platformService.getEntity(platformId);

        if (optionalMovie.isPresent() && optionalPlatform.isPresent()) {
            Movie movie = optionalMovie.get();
            Platform platform = optionalPlatform.get();

            // Add the movie to the platform
            movie.getMoviePlatforms().remove(platform);
            platform.getMovies().remove(movie);

            movieService.update(movie);
            platformService.update(platform);
            return new ResponseEntity<>("Movie removed from Platform.", HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>("Movie or Platform not found.", HttpStatusCode.valueOf(404));
        }
    }

    @PostMapping("/{movieId}/cast")
    public ResponseEntity<CastDTO> addCast(@PathVariable long movieId, @RequestBody CastDTO castDTO) {
        Optional<Movie> optionalMovie = movieService.getEntity(movieId);
        Optional<Person> optionalPerson = personService.getEntity(castDTO.getPerson_id());
        if (!optionalMovie.isPresent() || !optionalPerson.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(castDTO);
        }
        Movie movie = optionalMovie.get();
        Person person = optionalPerson.get();

        if (castService.existsByMovieAndPerson(movie, person)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(castDTO);
        }

        Cast cast = new Cast();
        cast.setRoleName(castDTO.getRoleName());
        cast.setMovie(movie);
        cast.setPerson(person);

        Cast savedCast = castService.create(cast);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToCastDTO(savedCast));
    }

    private CastDTO convertToCastDTO(Cast cast) {
        CastDTO result = new CastDTO();
        result = (CastDTO) result.convertToDTO(cast);
        return result;
    }

    @GetMapping("/{movieId}/cast")
    public List<CastDTO> list(@PathVariable long movieId) {
        Optional<Movie> optionalMovie = movieService.getEntity(movieId);
        if (!optionalMovie.isPresent()) {
            return Collections.emptyList();
        } else {
            Movie movie = optionalMovie.get();
            List<Cast> casts = new ArrayList<>(movie.getMovieCast());
            return casts.stream()
                    .map(this::convertToCastDTO)
                    .collect(Collectors.toList());
        }
    }

    @PutMapping("/{movieId}/cast")
    public ResponseEntity<CastDTO> updateCast(@PathVariable long movieId, @RequestBody CastDTO updatedCast) {
        Optional<Cast> optionalCast = castService.getEntity(updatedCast.getId());
        Optional<Movie> optionalMovie = movieService.getEntity(updatedCast.getMovie_id());
        Optional<Person> optionalPerson = personService.getEntity(updatedCast.getPerson_id());

        if (!optionalCast.isPresent() || !optionalPerson.isPresent() || !optionalMovie.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedCast);
        }
        if (movieId != optionalMovie.get().getId()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(updatedCast);
        }
        Cast cast = convertToCast(updatedCast);
        return ResponseEntity.status(HttpStatus.OK).body(convertToCastDTO(castService.update(cast)));
    }
    public Cast convertToCast(CastDTO castDTO){
        Cast cast= new Cast();
        cast.setMovie(movieService.getEntity(castDTO.getMovie_id()).get());
        cast.setId(castDTO.getId());
        cast.setPerson(personService.getEntity(castDTO.getPerson_id()).get());
        cast.setUpdatedAt(castDTO.getUpdatedAt());
        cast.setCreatedAt(castDTO.getCreatedAt());
        cast.setRoleName(castDTO.getRoleName());

        return cast;
    }
    @DeleteMapping("/{movieId}/cast/{castID}")
    public ResponseEntity<String> deleteCast(@PathVariable long castID, @PathVariable String movieId) {
        Optional<Cast> optionalCast = castService.getEntity(castID);

        if (!optionalCast.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cast not found");
        }
        castService.remove(optionalCast.get().getId());
        return ResponseEntity.status(HttpStatus.OK).body("Cast Deleted successfully");
    }

}
