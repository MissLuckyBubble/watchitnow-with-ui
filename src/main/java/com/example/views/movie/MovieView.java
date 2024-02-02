package com.example.views.movie;

import com.example.models.*;
import com.example.security.AuthenticatedUser;
import com.example.services.MovieService;
import com.example.views.MainLayout;
import com.example.views.movies.MoviesView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;

import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;



@PageTitle("Movie")
@Route(value = "movie", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class MovieView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final MovieService movieService;
    Movie movie;
    private AuthenticatedUser authenticatedUser;

    RatingDialog ratingDialog;

    public MovieView(MovieService movieService, AuthenticatedUser authenticatedUser) {
        this.movieService = movieService;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {


        QueryParameters parameters = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = parameters.getParameters();
        List<String> movieIdValues = parametersMap.get("movieId");
        String movieId = movieIdValues.get(0);

        if(movieId != null) {
            Optional<Movie> optionalMovie = movieService.getEntity(Long.parseLong(movieId));
            if (optionalMovie.isPresent()) {
                movie = optionalMovie.get();
            } else {
                return;
            }
        }else {
            return;
        }

        ratingDialog = new RatingDialog(movie);
        H1 title = new H1();
        getContent().add(title);
        title.setText(movie.getTitle());
        title.setWidth("max-content");

        createUnderTitleRow();


        HorizontalLayout layoutRow3 = new HorizontalLayout();

        Image posterImage = new Image(movie.getPoster_url(), "Poster");
        posterImage.setWidth("min-content");
        posterImage.setMinWidth("400px");
        posterImage.setHeight("500px");


        IFrame youtubePlayer = new IFrame(movie.getTrailer());
        youtubePlayer.setWidth("100%");
        youtubePlayer.setHeight("500px");
        youtubePlayer.setAllow("accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture");
        youtubePlayer.getElement().setAttribute("allowfullscreen", true);
        youtubePlayer.getElement().setAttribute("frameborder", "0");


        HorizontalLayout layoutRow4 = new HorizontalLayout();

        HorizontalLayout layoutRow5 = new HorizontalLayout();





        layoutRow3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.getStyle().set("flex-grow", "1");



        layoutRow4.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow4);
        layoutRow4.addClassName(Gap.MEDIUM);
        layoutRow4.setWidth("100%");
        layoutRow4.getStyle().set("flex-grow", "1");



        layoutRow5.setHeightFull();
        layoutRow4.setFlexGrow(1.0, layoutRow5);
        layoutRow5.addClassName(Gap.MEDIUM);
        layoutRow5.setWidth("100%");
        layoutRow5.getStyle().set("flex-grow", "1");
        layoutRow5.setAlignItems(Alignment.CENTER);
        layoutRow5.setJustifyContentMode(JustifyContentMode.END);


        getContent().add(layoutRow3);
        layoutRow3.add(posterImage);
        layoutRow3.add(youtubePlayer);
        getContent().add(layoutRow4);


        generateGenreButtons(layoutRow4);

        layoutRow4.add(layoutRow5);
        createWhereToWatch(layoutRow5);
        if(movie.getIsTvShow()) {
            createTVShowSection();
        }
        createCastColumn();
        createDescriptionColumn();
    }


    private void createTVShowSection() {
        VerticalLayout tvShowSection = new VerticalLayout();
        tvShowSection.setWidth("100%");

        H3 tvShowLabel = new H3("Seasons");
        tvShowLabel.setWidth("max-content");
        tvShowSection.add(tvShowLabel);

        UnorderedList seasons = new UnorderedList();

        List<Season> tvShowSeasons = movie.getSeasons().stream()
                .sorted(Comparator.comparing(Season::getFirstEpisodeRelease))
                .toList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        int totalNumberOfEpisodes = 0;

        for (Season season : tvShowSeasons) {

            totalNumberOfEpisodes += season.getNumberOfEpisodes();

            ListItem seasonItem = new ListItem();

            // Create a component to represent the season (e.g., Div, VerticalLayout)
            Div seasonInfo = new Div();

            // Add information about the season (customize based on your needs)
            seasonInfo.add(new H4("Season " + season.getSeasonNumber()));
            seasonInfo.add(new Paragraph("Number of Episodes: " + season.getNumberOfEpisodes()));
            seasonInfo.add(new Paragraph("Release Date: " + season.getFirstEpisodeRelease().format(formatter)));
            seasonInfo.add(new Paragraph("Last Episode Release Date: " + season.getLastEpisodeRelease().format(formatter)));

            // Add the component to the list item
            seasonItem.add(seasonInfo);

            // Add the list item to the unordered list
            seasons.add(seasonItem);
        }

        if(tvShowSeasons!= null && !tvShowSeasons.isEmpty()){
            Season firstSeason = tvShowSeasons.get(0);
            Season lastSeason = tvShowSeasons.get(tvShowSeasons.size() - 1);
            getContent()
                    .add(new H2("Tv series " +
                            firstSeason.getFirstEpisodeRelease().format(formatter)
                                    + " - "
                                    + lastSeason.getLastEpisodeRelease().format(formatter)));
        }
        getContent().add(new H3("Number of episodes: " + totalNumberOfEpisodes ));
        Details details = new Details("Number of seasons: " + tvShowSeasons.size(), seasons);
        details.setOpened(false);
        details.addThemeVariants(DetailsVariant.REVERSE);

        tvShowSection.add(details);
        getContent().add(tvShowSection);
    }


    private void createDescriptionColumn() {
        VerticalLayout descriptionColumn = new VerticalLayout();
        descriptionColumn.setWidthFull();
        descriptionColumn.setWidth("100%");
        descriptionColumn.getStyle().set("flex-grow", "1");
        getContent().setFlexGrow(1.0, descriptionColumn);

        H3 descriptionLabel = new H3();
        descriptionLabel.setText("Storyline");
        descriptionLabel.setWidth("max-content");

        Paragraph description = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        description.setText(movie.getDescription());
        description.setWidth("100%");
        description.getStyle().set("font-size", "var(--lumo-font-size-m)");

        getContent().add(descriptionColumn);
        descriptionColumn.add(descriptionLabel);
        descriptionColumn.add(description);
    }

    private void createCastColumn() {
        VerticalLayout castColumn = new VerticalLayout();
        castColumn.setWidth("100%");
        castColumn.getStyle().set("flex-grow", "1");
        castColumn.setWidthFull();
        getContent().add(castColumn);
        H3 castLabel = new H3();
        castLabel.setText("Cast");
        castLabel.setWidth("max-content");
        getContent().setFlexGrow(1.0, castColumn);
        castColumn.add(castLabel);
        setActorListData(castColumn);
    }

    private void createWhereToWatch(HorizontalLayout layoutRow5) {
        H4 whereToWatchLabel = new H4();
        whereToWatchLabel.setText("Where to watch:");
        whereToWatchLabel.setWidth("max-content");

        layoutRow5.add(whereToWatchLabel);

        for(Platform platform : movie.getPlatforms()){
            Image image = new Image();
            Anchor link = new Anchor();
            link.setHref(platform.getLink());
            link.setWidth("min-content");

            if (platform.getPicture() != null) {
                image.setSrc(platform.getPicture());
                image.setWidth("100px");
                link.add(image);
            }else {
                link.setText(platform.getLink());
            }

            layoutRow5.add(link);
        }
    }

    private void generateGenreButtons(HorizontalLayout layoutRow4) {
        for (MovieHasGenre movieGenre : movie.getGenres()) {
            String genreName = movieGenre.getGenre().getName();

            Button genreButton = new Button();
            genreButton.setText(genreName);
            genreButton.setWidth("min-content");

            // Create a RouterLink for navigation
            RouterLink genreLink = new RouterLink("", MoviesView.class);
            //RouterLink genreLink = new RouterLink("", MoviesView.class, movie.getId().toString());
            genreLink.getElement().getStyle().set("text-decoration", "none");
            genreLink.add(genreButton);

            // Add the RouterLink to the layout
            layoutRow4.add(genreLink);
        }
    }

    private void createUnderTitleRow() {
        HorizontalLayout underTitleRow = new HorizontalLayout();

        H2 releaseDateLabel = new H2();
        releaseDateLabel.setText("Release date:");
        releaseDateLabel.setWidth("max-content");
        underTitleRow.add(releaseDateLabel);

        createReleseDateText(underTitleRow, movie.getRelease_date().toString());


        VerticalLayout ratingColumn = new VerticalLayout();
        ratingColumn.setHeightFull();
        underTitleRow.setFlexGrow(1.0, ratingColumn);
        ratingColumn.addClassName(Gap.XSMALL);
        ratingColumn.setPadding(false);

        H6 ratingLabel = new H6();
        ratingLabel.setText("Rating:");
        ratingLabel.setWidth("max-content");
        ratingColumn.add(ratingLabel);

        ratingColumn.setAlignSelf(Alignment.END, ratingLabel);
        underTitleRow.add(ratingColumn);


        HorizontalLayout ratingNumberRow = new HorizontalLayout();

        ratingNumberRow.addClassName(Gap.MEDIUM);
        ratingNumberRow.setAlignItems(Alignment.END);
        underTitleRow.setAlignSelf(Alignment.END, ratingNumberRow);

        createRatingText(ratingNumberRow, String.valueOf(movie.getRate()));


        Icon ratingIcon = new Icon();
        ratingIcon.getElement().setAttribute("icon", "vaadin:star");
        ratingNumberRow.add(ratingIcon);

        Button rateButton = new Button();
        rateButton.setText("Rate");
        rateButton.setWidth("min-content");
        rateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        rateButton.addClickListener(event -> openRatingDialog());

        underTitleRow.setWidthFull();

        getContent().setFlexGrow(1.0, underTitleRow);
        underTitleRow.addClassName(Gap.MEDIUM);
        underTitleRow.setWidth("100%");
        underTitleRow.setHeight("60px");



        getContent().add(underTitleRow);
        ratingColumn.add(ratingNumberRow);
        underTitleRow.add(rateButton);
    }

    private static void createReleseDateText(HorizontalLayout underTitleRow, String text) {
        H2 releaseDateText = new H2();
        releaseDateText.setText(text);
        releaseDateText.setWidth("max-content");
        underTitleRow.add(releaseDateText);
    }

    private static void createRatingText(HorizontalLayout ratingNumberRow, String text) {
        H4 ratingText = new H4();
        ratingText.setText(text);
        ratingText.setWidth("20px");
        ratingText.setHeight("20px");
        ratingNumberRow.add(ratingText);
    }

    private void setActorListData(VerticalLayout verticalLayout) {
        for (Cast cast : movie.getCast()) {
            Person person = cast.getPerson();
            String roleName = cast.getRoleName();

            ActorListItem actorListItem = new ActorListItem(person, roleName);
            verticalLayout.add(actorListItem);
        }
    }

    private String getRoleForPerson(Person person) {
        // Assuming a person may have multiple roles in different movies
        // Adjust the logic based on your use case
        return movie.getCast().stream()
                .filter(cast -> cast.getPerson().equals(person))
                .map(Cast::getRoleName)
                .collect(Collectors.joining(", "));
    }

    private void openRatingDialog() {
        UI.getCurrent().navigate(MovieRatesView.class, QueryParameters.simple(Collections.singletonMap("movieId", String.valueOf(movie.getId()))));
    }



}
