package com.example.views.movie;

import com.example.models.UserRatesMovie;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;

public class MovieReviewCard extends Div {

    public MovieReviewCard(UserRatesMovie userRating) {
        addClassName("movie-review-card");

        Image userImage = new Image("https://robohash.org/" + userRating.getUser().getUsername(), "User Image");
        userImage.addClassName("img");

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.addClassName("content-layout");
        contentLayout.setSpacing(false);
        contentLayout.getThemeList().add("spacing-s");

        VerticalLayout content = new VerticalLayout();
        content.addClassName("content");
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);

        Span username = new Span(userRating.getUser().getUsername() + " ");
        username.addClassName("username");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String stringDate = userRating.getUpdatedAt() != null ? userRating.getUpdatedAt().format(formatter).toString() : userRating.getCreatedAt().format(formatter).toString();
        Span date = new Span(" " + stringDate);
        date.addClassName("date");

        header.add(username, date);

        Span comment = new Span(userRating.getComment());
        comment.addClassName("comment");

        Span rating = new Span("Rating: " + userRating.getRating() + " / 10 ");

        rating.add(createStarIcon(userRating.getRating()));
        rating.addClassName("rating");

        content.add(header, comment, rating);
        contentLayout.add(userImage, content);
        add(contentLayout);
    }

    private HorizontalLayout createStarIcon(int rating) {
        HorizontalLayout starLayout = new HorizontalLayout();

        for (int i = 0; i < rating; i++) {
            Icon starIcon = new Icon(VaadinIcon.STAR);
            starIcon.addClassName("icon");
            starLayout.add(starIcon);
        }

        return starLayout;
    }
}
