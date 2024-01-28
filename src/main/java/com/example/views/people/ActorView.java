package com.example.views.people;

import com.example.models.Cast;
import com.example.models.Movie;
import com.example.models.Person;
import com.example.services.PersonService;
import com.example.views.MainLayout;
import com.example.views.movie.MovieView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Actor")
@Route(value = "actor", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class ActorView extends Composite<HorizontalLayout> implements BeforeEnterObserver {

    private final PersonService personService;
    private Person person;
    private OrderedList imageContainer;

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
                getContent().removeAll();
                person = optionalPerson.get();
            } else {
                return;
            }
        } else {
            return;
        }

        VerticalLayout personLayout = getPersonLayout();
        personLayout.setClassName("personLayout");

        VerticalLayout seeMoreLayout = getSeeMoreLayout();
        seeMoreLayout.setClassName("seeMoreLayout");

        FlexLayout flexLayout = new FlexLayout(personLayout, seeMoreLayout);
        flexLayout.getStyle().set("flexWrap", "wrap");
        flexLayout.getStyle().set("flexWrap", "row");
        flexLayout.getStyle().set("justifyContent", "space-around");

// Add the FlexLayout to the content
        getContent().add(flexLayout);

// Define a CSS class for responsive styling
        flexLayout.addClassName("responsive-layout");
    }

    private VerticalLayout getPersonLayout() {
        VerticalLayout layout = new VerticalLayout();

        H1 title = new H1();
        layout.add(title);
        title.setText(person.getName() + " " + person.getLastName());
        title.setWidth("max-content");

        Image image = new Image();
        if (person.getPicture() != null) {
            image.setSrc(person.getPicture());
        }
        image.setWidth("25%");

        Paragraph paragraph = new Paragraph(person.getDetails());
        layout.add(image,paragraph);
        createActorDetails(layout);
        return layout;
    }

    private VerticalLayout getSeeMoreLayout() {
        VerticalLayout seeMoreLayout = new VerticalLayout();
        seeMoreLayout.addClassNames(LumoUtility.AlignItems.CENTER);
        seeMoreLayout.setWidth("50%");
        seeMoreLayout.setMaxWidth("50%");
        H2 seeMoreTitle = new H2("More to explore..");
        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        List<Person> morePeople = personService.findAll().stream()
                .filter(person1 -> person.getId() != person1.getId() && !person.getId().equals(person1.getId()))
                .sorted(Comparator.comparingInt(p -> -countCommonMovies(person, p))) // Sort by the number of common movies in descending order
                .limit(3)
                .collect(Collectors.toList());

        addActorsToImageContainer(morePeople);
        seeMoreLayout.add(seeMoreTitle,imageContainer);
        return seeMoreLayout;
    }

    private void createActorDetails(VerticalLayout personLayout) {
        VerticalLayout actorDetailsLayout = new VerticalLayout();
        actorDetailsLayout.setWidthFull();
        actorDetailsLayout.getStyle().set("flex-grow", "1");
        personLayout.setFlexGrow(1.0, actorDetailsLayout);

        H3 roleNameLabel = new H3();
        roleNameLabel.setText("Known for");
        roleNameLabel.setWidth("max-content");
        actorDetailsLayout.add(roleNameLabel);

        setRolesList(actorDetailsLayout);

        HtmlComponent br = new HtmlComponent("hr");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        H2 detailTitle = new H2("Personal Details");
        Span spanBorn = new Span("Birth date: " + person.getBirthDate().format(formatter));
        Span spanAge = new Span("Age: " + Period.between(person.getBirthDate(), LocalDate.now()).getYears());

        actorDetailsLayout.add(br,detailTitle,spanBorn,spanAge);
        personLayout.add(actorDetailsLayout);
    }

    private void setRolesList(VerticalLayout actorDetailsLayout) {
        Set<Cast> castList = person.getCasts();

        if (castList != null && !castList.isEmpty()) {
            for (Cast cast : castList) {
                String roleName = cast.getRoleName();
                String movieTitle = cast.getMovie().getTitle();

                H3 roleAndMovieLabel = new H3();
                roleAndMovieLabel.setText(roleName + " in " + movieTitle);
                roleAndMovieLabel.setWidth("max-content");

                roleAndMovieLabel.addClickListener(e -> navigateToMovie(cast.getMovie().getId()));

                actorDetailsLayout.add(roleAndMovieLabel);
            }
        } else {
            actorDetailsLayout.add(new H3("This actor has no roles."));
        }
    }

    private void navigateToMovie(Long movieId) {
        // Use UI.getCurrent().navigate() to navigate to MovieView
        UI.getCurrent().navigate(MovieView.class, QueryParameters.simple(Collections.singletonMap("movieId", String.valueOf(movieId))));
    }

    private void addActorsToImageContainer(List<Person> people) {
        imageContainer.removeAll();
        people.forEach(person -> {
            ActorCard actorCard = new ActorCard(person);
            actorCard.addClickListener(event -> {
                // Reload the page with a new ID
                Long id = person.getId();
                UI.getCurrent().navigate(ActorView.class, QueryParameters.simple(Collections.singletonMap("personID", String.valueOf(id))));

            });
            imageContainer.add(actorCard);
        });
    }

    private int countCommonMovies(Person person1, Person person2) {
        Set<Long> movieIds1 = person1.getCasts().stream().map(cast -> cast.getMovie().getId()).collect(Collectors.toSet());
        Set<Long> movieIds2 = person2.getCasts().stream().map(cast -> cast.getMovie().getId()).collect(Collectors.toSet());

        movieIds1.retainAll(movieIds2); // Retain only common movie IDs

        return movieIds1.size();
    }
}
