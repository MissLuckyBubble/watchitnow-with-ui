package com.example.views.admin.people;

import com.example.models.Movie;
import com.example.models.Person;
import com.example.services.*;
import com.example.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
@PageTitle("Admin-People")
@Route(value = "admin-people/:personID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AdminPeopleView extends Div implements BeforeEnterObserver {

    private final String PERSON_ID = "personID";
    private final String PERSON_EDIT_ROUTE_TEMPLATE = "admin-people/%s/edit";

    private final Grid<Person> grid = new Grid<>(Person.class, false);

    private TextField name;
    private TextField lastName;
    private DatePicker birthDate;
    private TextField picture;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private ConfirmDialog deleteDialog;

    private final BeanValidationBinder<Person> binder;

    private Person person;

    private final PersonService personService;

    public AdminPeopleView(PersonService personService) {
        this.personService = personService;
        addClassNames("admin-people-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true).setHeader("Name");
        grid.addColumn("lastName").setAutoWidth(true).setHeader("Last Name");
        grid.addColumn("birthDate").setAutoWidth(true).setHeader("Birth Date");
        grid.addColumn(new ComponentRenderer<>(person -> {
            Image image = new Image();
            if (person.getPicture() != null) {
                image.setSrc(person.getPicture());
            }
            image.setWidth("100px");
            return image;
        })).setHeader("Picture").setAutoWidth(true);

        grid.setItems(query -> {
            Pageable pageable = PageRequest.of(
                    query.getPage(),
                    query.getPageSize(),
                    VaadinSpringDataHelpers.toSpringDataSort(query)
            );
            return personService.list(pageable).getContent().stream();
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminPeopleView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Person.class);

        binder.bindInstanceFields(this);


        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        delete.addClickListener(e -> {
            deleteDialog.open();
        });


        save.addClickListener(e -> {
            try {
                if (this.person == null) {
                    this.person = new Person();
                    binder.writeBean(this.person);
                    personService.create(this.person);
                } else {
                    binder.writeBean(this.person);
                    personService.update(this.person);
                }
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AdminPeopleView.class);
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
        Optional<Long> optional = event.getRouteParameters().get(PERSON_ID).map(Long::parseLong);
        if (optional.isPresent()) {
            Optional<Person> optionalPerson = personService.getEntity(optional.get());
            if (optionalPerson.isPresent()) {
                populateForm(optionalPerson.get());
                deleteDialog = createDeleteDialog();
                delete.setEnabled(true);
            } else {
                Notification.show(
                        String.format("The requested person was not found, ID = %s", optional.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AdminPeopleView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");
        editorLayoutDiv.setMaxWidth("20%");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        lastName = new TextField("Last Name");
        picture = new TextField("Picture");
        birthDate = new DatePicker("Date Of birthday");
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setDateFormat("dd.MM.yyyy");
        birthDate.setI18n(i18n);
        formLayout.add(name,lastName, birthDate, picture);

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

    private void populateForm(Person value) {
        this.person = value;
        binder.readBean(this.person);

    }

    private ConfirmDialog createDeleteDialog() {
        return new ConfirmDialog("Confirm Delete",
                "Are you sure you want to delete " + person.getName() + "?",
                "Yes", event -> {
            // User clicked "Yes," perform deletion
            personService.remove(person.getId());
            clearForm();
            refreshGrid();
            Notification.show("Data deleted");
            UI.getCurrent().navigate(AdminPeopleView.class);
        },
                "No", event -> {
            deleteDialog.close();
        });
    }

}
