package com.example.views.admin.adminmovies;

import com.example.models.Cast;
import com.example.models.Movie;
import com.example.models.Person;
import com.example.services.CastService;
import com.example.services.MovieService;
import com.example.services.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.transaction.Transactional;

import java.util.Set;

public class MovieCastEditor extends Dialog {
    private final MovieService movieService;
    private final CastService castService;
    private final PersonService personService;
    private final Movie movie;
    private final Grid<Cast> castGrid = new Grid<>(Cast.class);
    public MovieCastEditor(MovieService movieService, Movie movie, CastService castService, PersonService personService) {
        this.movieService = movieService;
        this.movie = movie;
        this.castService = castService;
        this.personService = personService;

        this.setHeaderTitle("Edit The Cast of " + movie.getTitle());

        initializeContent();

        loadCast();

        this.add(castGrid);
        this.add(createActionButtons());
    }

    private void initializeContent() {
        castGrid.addColumn(cast -> cast.getPerson().getName()).setHeader("Name");
        castGrid.addColumn(cast -> cast.getPerson().getLastName()).setHeader(" Last Name");
        castGrid.addColumn(Cast::getRoleName).setHeader("Role Name");

        castGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        castGrid.setHeight("200px");
    }

    private void loadCast() {
        Set<Cast> movieCast = movie.getCast();

        castGrid.getColumns().forEach(castGrid::removeColumn);

        initializeContent();

        castGrid.getDataProvider().refreshAll();

        castGrid.setItems(movieCast);
    }

    @Transactional
    public VerticalLayout createActionButtons() {
        ComboBox<Person> castBox = new ComboBox<>();
        castBox.setItems(personService.findAll());
        castBox.setItemLabelGenerator(person -> person.getName() + " " + person.getLastName());

        TextField roleName = new TextField();

        Button addButton = new Button("Add", event -> addCast(castBox.getValue(),roleName.getValue()));

        Button removeButton = new Button("Remove", event -> removePlatformsFromMovie(castGrid.getSelectedItems()));

        HorizontalLayout inputLayout = new HorizontalLayout(castBox, roleName);
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, removeButton);
        VerticalLayout buttonFormLayout = new VerticalLayout(inputLayout, buttonLayout);

        buttonLayout.setSpacing(true);

        return buttonFormLayout;
    }

    @Transactional
    public void addCast(Person person, String roleName) {
        if (person != null && !hasExistingCast(person, roleName)) {
            Cast cast = new Cast();
            cast.setMovie(movie);
            cast.setPerson(person);
            cast.setRoleName(roleName);
            castService.create(cast);

            movie.getCast().add(cast);
            movieService.update(movie);

            person.getCasts().add(cast);
            personService.update(person);
            loadCast();
        } else {
            Notification.show(movie.getTitle() + " already has this person with the specified role.");
        }
    }

    @Transactional
    public void removePlatformsFromMovie(Set<Cast> casts) {
        if (casts != null && !casts.isEmpty()) {
            movie.getCast().removeAll(casts);
            for (Cast c : casts){
                c.getPerson().getCasts().remove(casts);
                personService.update(c.getPerson());
                castService.remove(c.getId());
            }

            movieService.update(movie);
            loadCast();
        }
    }

    private boolean hasExistingCast(Person person, String roleName) {
        for (Cast cast : movie.getCast()) {
            if (cast.getPerson().getId() == person.getId()
                    && cast.getRoleName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
