package com.example.views.movie;

import com.example.models.Person;
import com.example.views.movies.MoviesView;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class ActorListItem extends HorizontalLayout {

    public ActorListItem(Person person, String roleName) {
        setSpacing(true);

        // Create a circular image for the actor
        Image actorImage = new Image(person.getPicture(), person.getName());
        actorImage.setWidth("100px");
        actorImage.setHeight("100px");
        actorImage.getStyle().set("border-radius", "50%");

        // Create a container for the actor details
        VerticalLayout actorDetailsContainer = new VerticalLayout();

        // Create a Span for the actor's name
        Span actorNameSpan = new Span(person.getName() + " " + person.getLastName());
        actorNameSpan.getStyle().set("font-weight", "bold");

        setVerticalComponentAlignment(Alignment.CENTER, actorImage);

        // Create a Span for the role
        Span roleSpan = new Span("Role: " + roleName);

        HorizontalLayout wholeActorItemLayout = new HorizontalLayout();
        wholeActorItemLayout.add(actorImage, actorDetailsContainer);
        // Create a RouterLink for navigation to the person details page
        RouterLink link = new RouterLink();
        link.setRoute(MoviesView.class);
        link.add(wholeActorItemLayout);
        add(link);

        // Add actor details to the container
        actorDetailsContainer.add(actorNameSpan, roleSpan);
    }
}