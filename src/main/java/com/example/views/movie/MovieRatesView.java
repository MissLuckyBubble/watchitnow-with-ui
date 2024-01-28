package com.example.views.movie;

import com.example.models.Movie;
import com.example.models.User;
import com.example.models.UserRatesMovie;
import com.example.security.AuthenticatedUser;
import com.example.services.MovieService;
import com.example.services.UserRatesService;
import com.example.services.UserService;
import com.example.views.MainLayout;
import com.example.views.login.LoginView;
import com.example.views.movies.MoviesView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.*;

@PageTitle("Rating")
@Route(value = "movie-rates", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class MovieRatesView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final MovieService movieService;
    private final UserService userService;
    private final UserRatesService userRatesService;
    private Movie movie;
    private final AuthenticatedUser authenticatedUser;
    private User user;
    private UserRatesMovie userRatesMovie;


    RatingStarsComponent ratingStars;
    TextArea commentTextArea;

    public MovieRatesView(MovieService movieService, UserService userService, UserRatesService userRatesService, AuthenticatedUser authenticatedUser) {
        this.movieService = movieService;
        this.userService = userService;
        this.userRatesService = userRatesService;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

        QueryParameters parameters = beforeEnterEvent.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = parameters.getParameters();
        List<String> movieIdValues = parametersMap.get("movieId");
        String movieId = movieIdValues.get(0);


        if (!movieIdValues.isEmpty()) {
            Optional<Movie> optionalMovie = movieService.getEntity(Long.parseLong(movieId));
            movie = optionalMovie.orElse(null);
        } else {
            beforeEnterEvent.forwardTo(MoviesView.class);

        }

        Optional<User> optionalUser = authenticatedUser.get();
        optionalUser.ifPresent(value -> user = value);

        userRatesMovie = userRatesService.findByMovieAndUser(movie,user);

        initView();

    }

    private void initView() {

        HorizontalLayout titleLayout = createTitleLayout();

        H1 yourRatingTitle = new H1("Your Rating");
        if(userRatesMovie == null) {
            ratingStars = new RatingStarsComponent(1);
            commentTextArea = new TextArea("Comment");
        }
        else {
            ratingStars = new RatingStarsComponent(userRatesMovie.getRating());
            commentTextArea = new TextArea("Comment");
            commentTextArea.setValue(userRatesMovie.getComment());
        }

        commentTextArea.setWidth("25%");
        commentTextArea.setHeight("150px");

        Button submitButton = new Button("Submit", event -> submitRating());
        submitButton.getElement().getThemeList().add(Lumo.DARK);


        Grid<UserRatesMovie> movieReviewsGrid = createMovieReviewsGrid();
        movieReviewsGrid.addClassName("movie-reviews-view");

        List<UserRatesMovie> userRatings = userRatesService.findAllByMovie(movie);

        Comparator<UserRatesMovie> sortByUpdatedAt = Comparator
                .comparing(UserRatesMovie::getUpdatedAt, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(UserRatesMovie::getCreatedAt, Comparator.nullsFirst(Comparator.reverseOrder()));

        Collections.sort(userRatings, sortByUpdatedAt);

        movieReviewsGrid.setItems(userRatings);



        getContent().add(titleLayout, yourRatingTitle, ratingStars, commentTextArea, submitButton, movieReviewsGrid);
    }

    private Grid<UserRatesMovie> createMovieReviewsGrid() {
        Grid<UserRatesMovie> grid = new Grid<>();
        grid.addClassName("movie-reviews-grid");
        grid.addColumn(new ComponentRenderer<>(this::createMovieReviewCard)).setHeader("User Reviews for " + movie.getTitle());
        return grid;
    }

    private MovieReviewCard createMovieReviewCard(UserRatesMovie userRating) {
        return new MovieReviewCard(userRating);
    }

    private VerticalLayout crateTotalRateLayout() {
        VerticalLayout totalRateLayout = new VerticalLayout();
        H3 userRatingText = new H3("Total Rating: ");

        HorizontalLayout rating = new HorizontalLayout();
        Icon ratingIcon = new Icon();
        ratingIcon.getElement().setAttribute("icon", "vaadin:star");
        H4 numberRating = new H4(movie.getRate() + " / 10" );

        rating.add(ratingIcon, numberRating);

        H5 totalRates = new H5("Users Voted: " + movie.getMovieRatings().size());

        totalRateLayout.add(userRatingText,rating,totalRates);

        return totalRateLayout;
    }

    private HorizontalLayout createTitleLayout() {
        HorizontalLayout titleLayout = new HorizontalLayout();
        VerticalLayout titleRows = new VerticalLayout();
        Image posterImage = new Image(movie.getPoster_url(), "Poster");
        posterImage.setWidth("min-content");
        posterImage.setMinWidth("200px");
        posterImage.setHeight("300px");
        H2 movieTitle = new H2(movie.getTitle());
        H1 ratingTitle = new H1("REVIEWS");

        VerticalLayout totalRateLayout = crateTotalRateLayout();

        titleRows.add(movieTitle, ratingTitle, totalRateLayout);
        titleLayout.add(posterImage, titleRows);
        return titleLayout;
    }

    private void submitRating() {


        if (user == null) {
            UI.getCurrent().navigate(LoginView.class);
            Notification.show("Please log in to submit a rating");
        } else {
            int rating = ratingStars.getRating();
            String comment = commentTextArea.getValue();

            if(userRatesMovie == null) {
                userRatesMovie = new UserRatesMovie();
                userRatesMovie.setMovie(movie);
                userRatesMovie.setUser(user);

            }else {
                movie.getMovieRatings().remove(userRatesMovie);
                user.getRatedMovies().remove(userRatesMovie);
            }

            userRatesMovie.setRating(rating);
            userRatesMovie.setComment(comment);
            movie.getMovieRatings().add(userRatesMovie);
            user.getRatedMovies().add(userRatesMovie);




            if(userRatesMovie.getId() != null){
                userRatesService.update(userRatesMovie);


                Notification.show("Rating edited: " + rating + " for "
                        + movie.getTitle(), 3000, Notification.Position.BOTTOM_CENTER);
            }else {
                userRatesService.create(userRatesMovie);
                Notification.show("Rating submitted: " + rating + " for "
                        + movie.getTitle(), 3000, Notification.Position.BOTTOM_CENTER);
            }

                movieService.update(movie);
                userService.update(user);

            UI.getCurrent().getPage().reload();
        }
    }
}
