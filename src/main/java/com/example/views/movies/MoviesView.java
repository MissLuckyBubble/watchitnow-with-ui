package com.example.views.movies;

import com.example.models.Movie;
import com.example.services.MovieService;
import com.example.views.MainLayout;
import com.example.views.movie.MovieView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import java.util.Collections;
import java.util.List;

@PageTitle("Movies")
@Route(value = "movies", layout = MainLayout.class)
@AnonymousAllowed
public class MoviesView extends Main implements HasComponents, HasStyle {

    private OrderedList imageContainer;
    private final MovieService movieService;

    public MoviesView(MovieService movieService) {
        this.movieService = movieService;
        constructUI();

        // Load movies from the service and display them
        loadAndDisplayMovies();
    }

    private void loadAndDisplayMovies() {
        List<Movie> movies = movieService.findAll();
        movies.forEach(movie -> {
            MoviesViewCard movieCard = new MoviesViewCard(movie);
            movieCard.addClickListener(event -> {
                QueryParameters params
                        = QueryParameters.simple(Collections.singletonMap("movieId", movie.getId().toString()));
                UI.getCurrent().navigate(MovieView.class, params);
            });
            imageContainer.add(movieCard);
        });
    }




    private void constructUI() {
        addClassNames("movies-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Discover Amazing Movies");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Explore a collection of captivating movies from various genres.");description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, sortBy);
        add(container, imageContainer);
    }
}
