package com.example.views.admin.genres;
import com.example.models.Genre;
import com.example.services.GenreService;
import com.example.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Genres")
@Route(value = "genres/:genreID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class GenresView extends Div implements BeforeEnterObserver {

    private final String GENRE_ID = "genreID";
    private final String GENRE_EDIT_ROUTE_TEMPLATE = "genres/%s/edit";

    private final Grid<Genre> grid = new Grid<>(Genre.class, false);

    private TextField name;
    private TextField description;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private ConfirmDialog deleteDialog;


    private final BeanValidationBinder<Genre> binder;

    private Genre genre;

    private final GenreService genreService;

    public GenresView(GenreService genreService) {
        this.genreService = genreService;
        addClassNames("genres-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        delete.setEnabled(false);

        // Configure Grid
        grid.addColumn("name").setWidth("100px");
        grid.addColumn("description").setAutoWidth(true);

        grid.setItems(query -> genreService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(GENRE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(GenresView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Genre.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(e ->{
           deleteDialog.open();
        });

        save.addClickListener(e -> {

            try {
                System.out.println("Entering try block");
                if (this.genre == null) {
                    this.genre = new Genre();
                    binder.writeBean(this.genre);
                    genreService.create(this.genre);
                }else {
                    binder.writeBean(this.genre);
                    genreService.update(this.genre);
                }
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(GenresView.class);
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
        Optional<Long> genreID = event.getRouteParameters().get(GENRE_ID).map(Long::parseLong);
        if (genreID.isPresent()) {
            Optional<Genre> genreFromBackend = genreService.getEntity(genreID.get());
            if (genreFromBackend.isPresent()) {
                populateForm(genreFromBackend.get());
                deleteDialog = createDeleteDialog();
                delete.setEnabled(true);
            } else {
                Notification.show(
                        String.format("The requested genre was not found, ID = %s", genreID.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(GenresView.class);
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
        name = new TextField("Name");
        description = new TextField("Description");
        formLayout.add(name, description);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        delete.getStyle().set("color", "red");
        buttonLayout.add(save, cancel);
        if(genre==null) {
            buttonLayout.add(delete);
        }
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
        delete.setEnabled(false);
        deleteDialog = null;
    }

    private void populateForm(Genre value) {
        this.genre = value;
        binder.readBean(this.genre);

    }

    private ConfirmDialog createDeleteDialog(){
        return new ConfirmDialog("Confirm Delete",
                "Are you sure you want to delete " + genre.getName() + "?",
                "Yes", event -> {
            // User clicked "Yes," perform deletion
            genreService.remove(genre.getId());
            clearForm();
            refreshGrid();
            Notification.show("Data deleted");
            UI.getCurrent().navigate(GenresView.class);
        },
                "No", event -> {
            deleteDialog.close();
        });
    }

}