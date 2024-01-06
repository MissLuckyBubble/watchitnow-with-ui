package com.example.views.adminmovies;

import com.example.data.SamplePerson;
import com.example.models.Genre;
import com.example.models.Movie;
import com.example.services.MovieService;
import com.example.services.SamplePersonService;
import com.example.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;

import java.awt.geom.Area;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Admin-Movies")
@Route(value = "admin-movies/:movieID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AdminMoviesView extends Div implements BeforeEnterObserver {

    private final String MOVIE_ID = "movieID";
    private final String MOVIE_EDIT_ROUTE_TEMPLATE = "admin-movies/%s/edit";

    private final Grid<Movie> grid = new Grid<>(Movie.class, false);

    private TextField title;
    private DatePicker release_date;
    private TextField trailer;
    private TextField poster_url;
    private TextArea description;
    private ComboBox<Genre> genres = new ComboBox<>("Genre");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Movie> binder;

    private Movie movie;

    private final MovieService movieService;

    public AdminMoviesView(MovieService movieService) {
        this.movieService = movieService;
        addClassNames("admin-movies-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("title").setAutoWidth(true);
        grid.addColumn("release_date").setAutoWidth(true);
        grid.addColumn("trailer").setAutoWidth(true);
        grid.addColumn("poster_url").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("movieGenres").setAutoWidth(true);

        grid.setItems(query -> {
            Pageable pageable = PageRequest.of(
                    query.getPage(),
                    query.getPageSize(),
                    VaadinSpringDataHelpers.toSpringDataSort(query)
            );
            return movieService.list(pageable).getContent().stream();
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MOVIE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminMoviesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Movie.class);


        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.movie == null) {
                    this.movie = new Movie();
                }
                binder.writeBean(this.movie);
                movieService.update(this.movie);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AdminMoviesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> movieID = event.getRouteParameters().get(MOVIE_ID).map(Long::parseLong);
        if (movieID.isPresent()) {
            Optional<Movie> movieFromBackend = movieService.getEntity(movieID.get());
            if (movieFromBackend.isPresent()) {
                populateForm(movieFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested movie was not found, ID = %s", movieID.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AdminMoviesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        title = new TextField("Title");
        trailer = new TextField("Trailer");
        poster_url = new TextField("Poster");
        description = new TextArea("Description");
        release_date = new DatePicker("Date Of Release");
        genres = new ComboBox<Genre>("Genres");
        formLayout.add(title,trailer,poster_url,description,release_date,genres);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Movie value) {
        this.movie = value;
        binder.readBean(this.movie);

    }
}
