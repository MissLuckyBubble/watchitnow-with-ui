package com.example.views.movies;

import com.example.models.Genre;
import com.example.models.Movie;
import com.example.services.GenreService;
import com.example.services.MovieService;
import com.example.views.MainLayout;
import com.example.views.movie.MovieView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
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

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Movies")
@Route(value = "movies", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class MoviesView extends Main implements HasComponents, HasStyle {

    private OrderedList imageContainer;
    private final MovieService movieService;
    private final GenreService genreService;
    Select<String> sortBy;
    Select<Genre> genreSelect;
    TextField searchField;
    List<Movie> allMovies;

    public MoviesView(MovieService movieService, GenreService genreService) {
        this.movieService = movieService;
        this.genreService = genreService;
        constructUI();

        allMovies = movieService.findAll();
        loadAndDisplayMovies();
    }

    private void loadAndDisplayMovies() {
        List<Movie> movies = applySorting(allMovies);
        addMoviesToImageConteiner(movies);
    }

    private void addMoviesToImageConteiner(List<Movie> movies) {
        imageContainer.removeAll();
        movies.forEach(movie -> {
            MoviesViewCard movieCard = new MoviesViewCard(movie);
            movieCard.addClickListener(event -> {
                QueryParameters params = QueryParameters.simple(
                        Collections.singletonMap("movieId", movie.getId().toString()));
                UI.getCurrent().navigate(MovieView.class, params);
            });
            imageContainer.add(movieCard);
        });
    }

    private List<Movie> applySorting(List<Movie> movies) {

        String genreValue = sortBy.getValue().toString().trim().toLowerCase();
        switch (genreValue) {
            case "newest first":
                movies.sort((m1, m2) -> m2.getRelease_date().compareTo(m1.getRelease_date()));
                break;
            case "oldest first":
                movies.sort(Comparator.comparing(Movie::getRelease_date));
                break;
            case "rating":
                movies.sort((m1, m2) -> {
                    int ratingComparison = Double.compare(m2.getRate(), m1.getRate());
                    if (ratingComparison == 0) {
                        // If ratings are equal, compare by the number of votes
                        return Integer.compare(m2.getMovieRatings().size(), m1.getMovieRatings().size());
                    }
                    return ratingComparison;
                });
                break;
            default:

                break;
        }
        return movies;
    }

    private List<Movie> applyGenreFilter(List<Movie> movies) {

        Genre genreValue  = genreSelect.getValue();
        if (genreValue.getId() != -1L) {
            movies = movies.stream()
                    .filter(movie -> movie.getMovieGenres()
                            .stream()
                            .anyMatch(movieHasGenre ->
                                    movieHasGenre.getGenre().getId().equals(genreValue.getId())))
                    .collect(Collectors.toList());
        }
        return movies;
    }


    private void constructUI() {
        addClassNames("movies-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE);

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.addClassNames("movies-view");
        mainContainer.addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.MEDIUM, AlignItems.CENTER,JustifyContent.BETWEEN );

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();

        H2 header = new H2("Discover Amazing Movies");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Explore a collection of captivating movies from various genres.");description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Rating", "Newest first", "Oldest first");
        sortBy.setValue("Rating");
        sortBy.addValueChangeListener(event -> addMoviesToImageConteiner(applySorting(allMovies)));


        genreSelect = new Select<>();
        genreSelect.setLabel("Genre");
        Genre emptyGenreForAll = new Genre();
        emptyGenreForAll.setId(-1L);
        emptyGenreForAll.setName("All");
        List<Genre> allGenres = genreService.findAll();
        allGenres.add(emptyGenreForAll);
        genreSelect.setItems(allGenres);
        genreSelect.setValue(emptyGenreForAll);
        genreSelect.addValueChangeListener(event->addMoviesToImageConteiner(applyGenreFilter(allMovies)));

        genreSelect.setRenderer(new ComponentRenderer<>(genre -> {
            String displayText = genre.getName();  // Display the genre name
            String value = String.valueOf(genre.getId());  // Use the genre id as the value
            return new Div(displayText);  // You can customize this to display more information if needed
        }));

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        HorizontalLayout searchLayout = getSearchLayout();

        container.add(headerContainer, sortBy, genreSelect);
        mainContainer.add(container,searchLayout,imageContainer);
        add(mainContainer);
    }

    private HorizontalLayout getSearchLayout() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.addClassNames(LumoUtility.Width.FULL);

        searchField = new TextField();
        searchField.addClassName(LumoUtility.Width.FULL);
        searchField.setPlaceholder("Search a movie..");
        searchField.addValueChangeListener(event -> filterMovies(event.getValue()));

        Button searchButton = new Button("Search");
        searchButton.addClickListener(event -> filterMovies(searchField.getValue()));
        searchLayout.add(searchField, searchButton);
        return searchLayout;
    }

    private void filterMovies(String filter) {
        List<Movie> filteredMovies = allMovies.stream()
                .filter(movie ->
                        (movie.getTitle() + " " + movie.getRelease_date().format(DateTimeFormatter.BASIC_ISO_DATE)).toLowerCase().contains(filter.toLowerCase()) ||
                        movie.getDescription().toLowerCase().contains(filter.toLowerCase())||
                        movie.getMovieCast().stream().anyMatch(
                                cast -> (cast.getPerson().getName() + " " + cast.getPerson().getLastName()).toLowerCase().contains(filter.toLowerCase())
                        ))
                .collect(Collectors.toList());
        addMoviesToImageConteiner(applySorting(applyGenreFilter(filteredMovies)));
    }
}
