package com.example.views.admin.platform;

import com.example.models.Platform;
import com.example.services.PlatformService;
import com.example.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@PageTitle("Platforms")
@Route(value = "admin-platforms/:platformId?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class PlatformView  extends Div implements BeforeEnterObserver {

    private final String PLATFORM_ID = "platformId";
    private final String PLATFORM_EDIT_ROUTE_TEMPLATE = "admin-platforms/%s/edit";

    private final Grid<Platform> grid = new Grid<>(Platform.class, false);

    private TextField title;
    private TextField link;
    private TextField picture;


    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private ConfirmDialog deleteDialog;

    private final BeanValidationBinder<Platform> binder;

    private Platform platform;

    private final PlatformService platformService;

    public PlatformView(PlatformService platformService) {
        this.platformService = platformService;
        addClassNames("admin-platforms");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
        delete.setEnabled(false);
        // Configure Grid
        grid.addColumn("title").setWidth("150px");
        grid.addColumn("link").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(platform -> {
            Image image = new Image();
            if (platform.getPicture() != null) {
                image.setSrc(platform.getPicture());
            }
            image.setWidth("100px");
            return image;
        })).setHeader("Picture").setAutoWidth(true);

        grid.setItems(query -> platformService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PLATFORM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PlatformView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Platform.class);

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
                if (this.platform == null) {
                    this.platform = new Platform();
                }
                binder.writeBean(this.platform);
                platformService.create(this.platform);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(PlatformView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> optionalPlatfrom = event.getRouteParameters().get(PLATFORM_ID).map(Long::parseLong);
        if (optionalPlatfrom.isPresent()) {
            Optional<Platform> platformServiceEntity = platformService.getEntity(optionalPlatfrom.get());
            if (platformServiceEntity.isPresent()) {
                populateForm(platformServiceEntity.get());
                deleteDialog = createDeleteDialog();
                delete.setEnabled(true);
            } else {
                Notification.show(
                        String.format("The requested Platform was not found, ID = %s", optionalPlatfrom.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PlatformView.class);
            }
        }
    }


    private ConfirmDialog createDeleteDialog(){
        return new ConfirmDialog("Confirm Delete",
                "Are you sure you want to delete " + platform.getTitle() + "?",
                "Yes", event -> {
            platformService.remove(platform.getId());
            clearForm();
            refreshGrid();
            Notification.show("Data deleted");
            UI.getCurrent().navigate(PlatformView.class);
        },
                "No", event -> {
            deleteDialog.close();
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");
        editorLayoutDiv.setMaxWidth("20%");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        title = new TextField("Title");
        link = new TextField("Link");
        picture = new TextField("Picture");
        formLayout.add(title,link,picture);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setMinHeight("90%");
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        delete.getStyle().set("color", "red");
        buttonLayout.add(save, cancel);
        if(platform==null) {
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
    }

    private void populateForm(Platform value) {
        this.platform = value;
        binder.readBean(this.platform);

    }
}