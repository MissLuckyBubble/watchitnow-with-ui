package com.example.views.movie;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

@Tag("div")
@StyleSheet("frontend://styles/rating-stars.css")
public class RatingStarsComponent extends Div {

    private int rating;

    public RatingStarsComponent(int rating) {
        this.rating = rating;
        updateStars();
        addClickListener(this::handleClick);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating >= 0 && rating <= 10) {
            this.rating = rating;
            updateStars();
        }
    }

    private void updateStars() {
        // Clear existing stars
        removeAll();

        // Add stars with appropriate styles based on the rating
        for (int i = 0; i < 10; i++) {
            Icon starIcon = (i < rating) ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
            int clickedStar = i + 1 ;
            starIcon.addClickListener(e -> handleStarClick(clickedStar));
            add(starIcon);
        }
    }

    private void handleClick(ClickEvent<? extends Component> event) {
        // Calculate the clicked star based on the click position
        int clickedStar = executeJavaScript("return Math.ceil((arguments[0].clientX - arguments[1].getBoundingClientRect().left) / arguments[2]);",
                event, getElement(), getStarWidth());

        // Update the rating
        setRating(clickedStar);
    }

    private native int executeJavaScript(String script, Object... args) /*-{
    return $wnd.Vaadin.Flow.clients[$wnd.Vaadin.Flow.getFlowIdFromDomTree(args[0].node)].incrementalId++
        .map(function (id) {
            return $wnd.Vaadin.Flow.getConnectorRegistry().getConnector(id);
        })
        .map(function (connector) {
            return $wnd.Vaadin.Flow.fireEvent(connector, 'dom-change', {});
        });
}-*/;

    private void handleStarClick(int clickedStar) {
        // Update the rating
        setRating(clickedStar);
    }

    private double getStarWidth() {
        return 100;
    }
}
