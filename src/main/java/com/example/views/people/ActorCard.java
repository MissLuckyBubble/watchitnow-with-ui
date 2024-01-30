package com.example.views.people;

import com.example.models.Cast;
import com.example.models.Person;
import com.example.models.TvShow;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class ActorCard extends ListItem {

    public ActorCard(Person actor){
        String actorFullName = actor.getName() + " " + actor.getLastName();
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        Div div = getDiv(actor, actorFullName);

        Span header = new Span();
        header.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
        header.setText(actorFullName);

        Span subtitle = getSpan(actor);

        Paragraph description = new Paragraph(actor.getDetails());
        description.addClassName(LumoUtility.Margin.Vertical.MEDIUM);


        add(div, header, subtitle, description);

    }

    private static Span getSpan(Person actor) {
        Span subtitle = new Span();
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        String castString = "";
        for (Cast cast : actor.getCasts()) {
            if (cast != null && cast.getMovie() != null) {
                castString += cast.getMovie().getTitle()
                        + " ("
                        + cast.getMovie().getRelease_date().getYear()
                        + "), ";
            }
        }

        if (castString != null && !castString.isEmpty()) {
            castString = castString.substring(0, castString.length() - 2);
        }

        subtitle.setText("Staring in: " + castString );
        return subtitle;
    }

    private static Div getDiv(Person actor, String actorFullName) {
        Div div = new Div();
        div.addClassNames(LumoUtility.Background.CONTRAST, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
        div.setHeight("160px");

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(actor.getPicture());
        image.setAlt(actorFullName);

        div.add(image);
        return div;
    }
}
