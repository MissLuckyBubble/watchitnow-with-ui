package com.example.views.admin.seasons;

import com.example.models.Movie;
import com.example.models.Season;
import com.example.services.*;
import com.example.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("Admin-Seasons")
@Route(value = "admin-seasons/:seasonId?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AdminSeasonsView extends Div implements BeforeEnterObserver {

    private final String SEASON_ID = "seasonId";
    private final String SEASON_EDIT_ROUTE_TEMPLATE = "admin-seasons/%s/edit";

    private final Grid<Season> grid = new Grid<>(Season.class, false);

    private IntegerField seasonNumber;
    private IntegerField numberOfEpisodes;
    private DatePicker firstEpisodeRelease;
    private DatePicker lastEpisodeRelease;
    private ComboBox<Movie> movieComboBox;



    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private ConfirmDialog deleteDialog;


    private final BeanValidationBinder<Season> binder;

    private Season season;

    private final SeasonService seasonService;
    private final MovieService movieService;

    public AdminSeasonsView(SeasonService seasonService, MovieService movieService) {

        this.seasonService = seasonService;
        this.movieService = movieService;
        addClassNames("admin-seasons-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn(season1 -> {
            Movie movie = season1.getMovie();
            return movie != null ? movie.getTitle() : "";
        }).setAutoWidth(true).setHeader("TV Show");
        grid.addColumn("seasonNumber").setAutoWidth(true).setHeader("Season Number");
        grid.addColumn("numberOfEpisodes").setAutoWidth(true).setHeader("Number of Episodes ");
        grid.addColumn("firstEpisodeRelease").setWidth("100px").setHeader("Release Date");
        grid.addColumn("lastEpisodeRelease").setWidth("100px").setHeader("Last Episode Date");


        grid.setItems(query -> {
            Pageable pageable = PageRequest.of(
                    query.getPage(),
                    query.getPageSize(),
                    VaadinSpringDataHelpers.toSpringDataSort(query)
            );
            return seasonService.list(pageable).getContent().stream();
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SEASON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminSeasonsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Season.class);

        binder.bindInstanceFields(this);
        binder.forField(movieComboBox).bind(Season::getMovie, Season::setMovie);


        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        delete.addClickListener(e -> {
            deleteDialog.open();
        });



        save.addClickListener(e -> {
            try {
                if (this.season == null) {
                    this.season = new Season();
                    binder.writeBean(this.season);
                    seasonService.create(this.season);
                } else {
                    binder.writeBean(this.season);
                    seasonService.update(this.season);
                }
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AdminSeasonsView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            } catch (Exception exc) {
                exc.printStackTrace();  // Log or print the exception details
                // Handle the exception or log information for further debugging
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> seasonId = event.getRouteParameters().get(SEASON_ID).map(Long::parseLong);
        if (seasonId.isPresent()) {
            Optional<Season> optionalSeason = seasonService.getEntity(seasonId.get());
            if (optionalSeason.isPresent()) {
                populateForm(optionalSeason.get());
                deleteDialog = createDeleteDialog();
                delete.setEnabled(true);
            } else {
                Notification.show(
                        String.format("The requested season was not found, ID = %s", seasonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(AdminSeasonsView.class);
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
        numberOfEpisodes = new IntegerField("Number Of Episodes");
        seasonNumber = new IntegerField("Season Number");
        firstEpisodeRelease = new DatePicker("First Episode Release");
        lastEpisodeRelease = new DatePicker("Last Episode Release");
        movieComboBox = new ComboBox<>("Select Movie");
        movieComboBox.setItems(movieService.findAll().stream().filter(movie -> movie.getIsTvShow()).collect(Collectors.toList()));
        movieComboBox.setItemLabelGenerator(Movie::getTitle);

        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setDateFormat("dd.MM.yyyy");
        firstEpisodeRelease.setI18n(i18n);
        lastEpisodeRelease.setI18n(i18n);
        formLayout.add(seasonNumber, numberOfEpisodes, firstEpisodeRelease, lastEpisodeRelease, movieComboBox);

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

        HorizontalLayout actionsLayout = new HorizontalLayout(delete);
        wrapper.add(actionsLayout);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
        delete.setEnabled(false);
        deleteDialog = null;

    }

    private void populateForm(Season value) {
        this.season = value;
        binder.readBean(this.season);
        movieComboBox.setItems(movieService.findAll().stream().filter(movie -> movie.getIsTvShow()).collect(Collectors.toList()));


    }

    private ConfirmDialog createDeleteDialog() {
        return new ConfirmDialog("Confirm Delete",
                "Are you sure you want to delete " + season.getSeasonNumber() + "?",
                "Yes", event -> {
            // User clicked "Yes," perform deletion
            seasonService.remove(season.getId());
            clearForm();
            refreshGrid();
            Notification.show("Data deleted");
            UI.getCurrent().navigate(AdminSeasonsView.class);
        },
                "No", event -> {
            deleteDialog.close();
        });
    }

}
