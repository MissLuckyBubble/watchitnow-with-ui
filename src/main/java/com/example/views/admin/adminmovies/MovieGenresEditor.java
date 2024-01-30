package com.example.views.admin.adminmovies;

import com.example.models.Genre;
import com.example.models.Movie;
import com.example.models.MovieHasGenre;
import com.example.services.GenreService;
import com.example.services.MovieHasGenreService;
import com.example.services.MovieService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


public class MovieGenresEditor extends Dialog {
    private final MovieService movieService;
    private final GenreService genreService;
    private final Movie movie;
    private final Grid<Genre> genresGrid = new Grid<>(Genre.class);
    private final MovieHasGenreService movieHasGenreService;
    public MovieGenresEditor(MovieService movieService, GenreService genreService, Movie movie, MovieHasGenreService movieHasGenreService) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.movie = movie;
        this.movieHasGenreService = movieHasGenreService;

        this.setHeaderTitle("Edit Genres of " + movie.getTitle());

        initializeContent();

        loadGenres();

        this.add(genresGrid);
        this.add(createActionButtons());
    }

    private void initializeContent() {
        genresGrid.setColumns("name", "description");
        genresGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        genresGrid.setHeight("200px");
    }

    private void loadGenres() {
        Set<Genre> movieGenres = movie.getGenres().stream()
                .map(MovieHasGenre::getGenre)
                .collect(Collectors.toSet());

        genresGrid.getDataProvider().refreshAll();

        genresGrid.setItems(movieGenres);
    }

    @Transactional
    public HorizontalLayout createActionButtons() {
        ComboBox<Genre> genreComboBox = new ComboBox<>();
        genreComboBox.setItems(genreService.findAll());
        genreComboBox.setItemLabelGenerator(Genre::getName);

        Button addButton = new Button("Add", event -> addGenresToMovie(genreComboBox.getValue()));

        Button removeButton = new Button("Remove", event -> removeGenresFromMovie(genresGrid.getSelectedItems()));

        HorizontalLayout buttonLayout = new HorizontalLayout(genreComboBox, addButton, removeButton);
        buttonLayout.setSpacing(true);

        return buttonLayout;
    }

    @Transactional
    public void addGenresToMovie(Genre genre) {
        if (genre != null) {
            if (movie.getGenres().stream().anyMatch(mhg -> mhg.getGenre().getId() == genre.getId())) {
                Notification.show(movie.getTitle() + " already has this genre.");
                return;
            }
            MovieHasGenre movieHasGenre = new MovieHasGenre(movie, genre);
            movieHasGenreService.create(movieHasGenre);
            movie.getGenres().add(movieHasGenre);
            loadGenres();
        }
    }

    @Transactional
    public void removeGenresFromMovie(Set<Genre> selectedGenres) {
        if (!selectedGenres.isEmpty()) {
            Set<MovieHasGenre> movieHasGenresToRemove = selectedGenres.stream()
                    .flatMap(genre -> movie.getGenres().stream()
                            .filter(movieHasGenre -> movieHasGenre.getGenre().equals(genre)))
                    .collect(Collectors.toSet());

            for (MovieHasGenre movieHasGenre : movieHasGenresToRemove) {
                movieHasGenreService.remove(movieHasGenre.getId());
            }
            movie.getGenres().removeAll(movieHasGenresToRemove);
            movieService.update(movie);
            loadGenres();
        }
    }


}
