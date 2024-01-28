package com.example.views.people;

import com.example.models.Genre;
import com.example.models.Person;
import com.example.services.PersonService;
import com.example.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Actors")
@Route(value = "actors", layout = MainLayout.class)
@AnonymousAllowed
public class ActorsView extends Main implements HasComponents, HasStyle {

    private OrderedList imageContainer;
    private final PersonService personService;
    Select<String> sortBy;
    TextField searchField;
    List<Person> allActors;

    public ActorsView(PersonService personService) {
        this.personService = personService;
        constructUI();

        allActors = personService.findAll();
        loadAndDisplayPeople();
    }

    private void loadAndDisplayPeople() {
        List<Person> personList = applySorting(allActors);
        addActorsToImageContainer(personList);
    }

    private void addActorsToImageContainer(List<Person> people) {

        imageContainer.removeAll();
        people.forEach(person -> {
            ActorCard actorCard = new ActorCard(person);
            actorCard.addClickListener(event -> {
                QueryParameters params = QueryParameters.simple(
                        Collections.singletonMap("personID", person.getId().toString()));
                UI.getCurrent().navigate(ActorView.class, params);
            });
            imageContainer.add(actorCard);
        });
    }

    private List<Person> applySorting(List<Person> people) {

        String sortValue = sortBy.getValue().toString().trim().toLowerCase();
        switch (sortValue) {
            case "age":
                people.sort((p1, p2) -> p2.getBirthDate().compareTo(p1.getBirthDate()));
                break;
            case "a-z":
                people.sort(Comparator.comparing(Person::getName).thenComparing(Person::getLastName));
                break;
            case "z-a":
                people.sort(Comparator.comparing(Person::getName).thenComparing(Person::getLastName).reversed());
                break;
            default:

                break;
        }
        return people;
    }


    private void constructUI() {
        addClassNames("actors-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE);

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.addClassNames("actors-view");
        mainContainer.addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.MEDIUM, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN );

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();

        H2 header = new H2("See your favorite actors");
        header.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE, LumoUtility.FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Discover talented actors who bring characters to life with their exceptional performances.");
        headerContainer.add(header, description);

        sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("A-Z", "Z-A", "Age");
        sortBy.setValue("A-Z");
        sortBy.addValueChangeListener(event -> addActorsToImageContainer(applySorting(allActors)));

        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);

        HorizontalLayout searchLayout = getSearchLayout();

        container.add(headerContainer, sortBy);
        mainContainer.add(container,searchLayout,imageContainer);
        add(mainContainer);
    }

    private HorizontalLayout getSearchLayout() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.addClassNames(LumoUtility.Width.FULL);

        searchField = new TextField();
        searchField.addClassName(LumoUtility.Width.FULL);
        searchField.setPlaceholder("Search an actor..");
        searchField.addValueChangeListener(event -> filterActors(event.getValue()));

        Button searchButton = new Button("Search");
        searchButton.addClickListener(event -> filterActors(searchField.getValue()));
        searchLayout.add(searchField, searchButton);
        return searchLayout;
    }

    private void filterActors(String filter) {
        String filterLC = filter.toLowerCase();
        List<Person> filteredActors = allActors.stream()
                .filter(person ->
                        (person.getName() + " " + person.getLastName()).toLowerCase().contains(filterLC)||
                        person.getCasts().stream().anyMatch(cast -> cast.getMovie().getTitle().toLowerCase().contains(filterLC))
                       )
                .collect(Collectors.toList());

        addActorsToImageContainer(applySorting(filteredActors));

    }
}
