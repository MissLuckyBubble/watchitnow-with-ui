package com.example.views.movie;

import com.example.models.Movie;
import com.example.models.UserRatesMovie;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import de.jfancy.StarsRating;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RatingDialog extends Dialog {

    Movie movie;
    UserRatesMovie userRatesMovie;

    private final static Map<Integer, String> valueCaptions = new HashMap<>(5, 1);

    static {
        valueCaptions.put(1, "Epic Fail");
        valueCaptions.put(2, "Poor");
        valueCaptions.put(3, "OK");
        valueCaptions.put(4, "Good");
        valueCaptions.put(5, "Excellent");
    }

    public RatingDialog(Movie movie) {
        this.movie = movie;

        StarsRating starsRating = new StarsRating();
        starsRating.setVisible(true);
        starsRating.addValueChangeListener(e -> {
            int ratingValue = Math.round(starsRating.getValue()); // Round to the nearest integer
            System.out.println("Value has changed to: " + ratingValue);
        });

        Button btn = new Button("Test Button");
        btn.addClickListener(e -> {
            int ratingValue = Math.round(starsRating.getValue()); // Round to the nearest integer
            System.out.println("Value: " + ratingValue);
        });

        add(new H3("Rate the Movie"), starsRating, btn);
    }
}
