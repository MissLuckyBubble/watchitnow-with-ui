package com.example.views.movies;

import com.example.models.Cast;
import com.example.models.Genre;
import com.example.models.Movie;
import com.example.models.MovieHasGenre;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

public class MoviesViewCard extends ListItem {

    public MoviesViewCard(Movie movie) {
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(movie.getPoster_url());
        image.setAlt(movie.getTitle());

        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        header.setText(movie.getTitle() + " (" + movie.getRelease_date().getYear() + ")");

        Span subtitle = new Span();
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        String castString = "";
        for (Cast cast : movie.getMovieCast()) {
            castString += cast.getPerson().getName() + " " + cast.getPerson().getLastName() + ", ";
        }

        if (!castString.isEmpty()) {
            castString = castString.substring(0, castString.length() - 2);
        }
        subtitle.setText("Stars: " + castString );

        Paragraph description = new Paragraph(movie.getDescription());
        description.addClassName(Margin.Vertical.MEDIUM);

        HorizontalLayout genreLayout = new HorizontalLayout();
        for (MovieHasGenre genre: movie.getMovieGenres())
        {
            Span badge = new Span();
            badge.getElement().setAttribute("theme", "badge");
            badge.setText(genre.getGenre().getName());
            genreLayout.add(badge);
        }


        add(div, header, subtitle, description,genreLayout);

    }
}
