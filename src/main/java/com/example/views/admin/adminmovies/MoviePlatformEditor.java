package com.example.views.admin.adminmovies;

import com.example.models.Movie;
import com.example.models.Platform;
import com.example.services.MovieService;
import com.example.services.PlatformService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import jakarta.transaction.Transactional;

import java.util.Set;

public class MoviePlatformEditor extends Dialog {
    private final MovieService movieService;
    private final PlatformService platformService;
    private final Movie movie;
    private final Grid<Platform> platformGrid = new Grid<>(Platform.class);
    public MoviePlatformEditor(MovieService movieService, Movie movie, PlatformService platformService) {
        this.movieService = movieService;
        this.movie = movie;
        this.platformService = platformService;

        this.setHeaderTitle("Edit Platforms of " + movie.getTitle());

        initializeContent();

        loadPlatforms();

        this.add(platformGrid);
        this.add(createActionButtons());
    }

    private void initializeContent() {
        platformGrid.setColumns("title", "link", "picture");
        platformGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        platformGrid.setHeight("200px");
    }

    private void loadPlatforms() {
        Set<Platform> moviePlatforms = movie.getMoviePlatforms();

        platformGrid.getDataProvider().refreshAll();

        platformGrid.setItems(moviePlatforms);
    }

    @Transactional
    public HorizontalLayout createActionButtons() {
        ComboBox<Platform> platfromBox = new ComboBox<>();
        platfromBox.setItems(platformService.findAll());
        platfromBox.setItemLabelGenerator(Platform::getTitle);

        Button addButton = new Button("Add", event -> addPlatform(platfromBox.getValue()));

        Button removeButton = new Button("Remove", event -> removePlatformsFromMovie(platformGrid.getSelectedItems()));

        HorizontalLayout buttonLayout = new HorizontalLayout(platfromBox, addButton, removeButton);
        buttonLayout.setSpacing(true);

        return buttonLayout;
    }

    @Transactional
    public void addPlatform(Platform platform) {
        if (platform != null) {
            if (movie.getMoviePlatforms().contains(platform)) {
                Notification.show(movie.getTitle() + " already has this platform.");
                return;
            }
            movie.getMoviePlatforms().add(platform);
            platform.getMovies().add(movie);

            movieService.update(movie);
            platformService.update(platform);
            loadPlatforms();
        }
    }

    @Transactional
    public void removePlatformsFromMovie(Set<Platform> platforms) {
        if (!platforms.isEmpty()) {
            movie.getMoviePlatforms().removeAll(platforms);
            movieService.update(movie);
            loadPlatforms();
        }
    }


}
