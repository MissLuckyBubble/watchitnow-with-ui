package com.example.views.people;

import com.example.models.Cast;
import com.example.models.Person;
import com.example.services.PersonService;
import com.example.views.MainLayout;
import com.example.views.movie.MovieView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.*;

@PageTitle("Actor")
@Route(value = "actor", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class ActorView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final PersonService personService;
    private Person person;

    public ActorView(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters parameters = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = parameters.getParameters();
        List<String> personIdValues = parametersMap.get("personID");
        String personId = personIdValues.get(0);

        if (personId != null) {
            Optional<Person> optionalPerson = personService.getEntity(Long.parseLong(personId));
            if (optionalPerson.isPresent()) {
                person = optionalPerson.get();
            } else {
                return;
            }
        } else {
            return;
        }

        H1 title = new H1();
        getContent().add(title);
        title.setText(person.getName() + " " + person.getLastName());
        title.setWidth("max-content");

        Image image = new Image();
        if (person.getPicture() != null) {
            image.setSrc(person.getPicture());
        }
        image.setWidth("50%");
        getContent().add(image);
        createActorDetails();
    }

    private void createActorDetails() {
        VerticalLayout actorDetailsLayout = new VerticalLayout();
        actorDetailsLayout.setWidthFull();
        actorDetailsLayout.getStyle().set("flex-grow", "1");
        getContent().setFlexGrow(1.0, actorDetailsLayout);

        H3 roleNameLabel = new H3();
        roleNameLabel.setText("Known for");
        roleNameLabel.setWidth("max-content");
        actorDetailsLayout.add(roleNameLabel);

        setRolesList(actorDetailsLayout);

        getContent().add(actorDetailsLayout);
    }

    private void setRolesList(VerticalLayout actorDetailsLayout) {
        Set<Cast> castList = person.getCasts();

        if (!castList.isEmpty()) {
            for (Cast cast : castList) {
                String roleName = cast.getRoleName();
                String movieTitle = cast.getMovie().getTitle();

                // Create a clickable component to display the role and movie information
                H3 roleAndMovieLabel = new H3();
                roleAndMovieLabel.setText(roleName + " in " + movieTitle);
                roleAndMovieLabel.setWidth("max-content");

                // Set up the navigation to MovieView when clicked
                roleAndMovieLabel.addClickListener(e -> navigateToMovie(cast.getMovie().getId()));

                actorDetailsLayout.add(roleAndMovieLabel);
            }
        } else {
            // Display a message if the actor has no roles
            actorDetailsLayout.add(new H3("This actor has no roles."));
        }
    }

    private void navigateToMovie(Long movieId) {
        // Use UI.getCurrent().navigate() to navigate to MovieView
        UI.getCurrent().navigate(MovieView.class, QueryParameters.simple(Collections.singletonMap("movieId", String.valueOf(movieId))));
    }
}
