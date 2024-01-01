package com.example.views.movie;

import com.example.components.avataritem.AvatarItem;
import com.example.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import java.util.List;

@PageTitle("Movie")
@Route(value = "my-view", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class MovieView extends Composite<VerticalLayout> {

    public MovieView() {

        H1 title = new H1();
        getContent().add(title);

        createUnderTitleRow();


        HorizontalLayout layoutRow3 = new HorizontalLayout();
        Span badge = new Span();
        Span badge2 = new Span();
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        Button buttonSecondary = new Button();
        Button buttonSecondary2 = new Button();
        HorizontalLayout layoutRow5 = new HorizontalLayout();
        H4 h42 = new H4();
        Anchor link = new Anchor();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        H3 h3 = new H3();
        MultiSelectListBox avatarItems = new MultiSelectListBox();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        H3 h32 = new H3();
        Paragraph textMedium = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        title.setText("Title");
        title.setWidth("max-content");

        layoutRow3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.getStyle().set("flex-grow", "1");
        badge.setText("Poster");
        badge.setWidth("min-content");
        badge.setMinWidth("400px");
        badge.setHeight("500px");
        badge.getElement().getThemeList().add("badge");
        badge2.setText("Trailer");
        badge2.getStyle().set("flex-grow", "1");
        badge2.setHeight("500px");
        badge2.getElement().getThemeList().add("badge");
        layoutRow4.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow4);
        layoutRow4.addClassName(Gap.MEDIUM);
        layoutRow4.setWidth("100%");
        layoutRow4.getStyle().set("flex-grow", "1");
        buttonSecondary.setText("Ganre");
        buttonSecondary.setWidth("min-content");
        buttonSecondary2.setText("Ganre");
        buttonSecondary2.setWidth("min-content");
        layoutRow5.setHeightFull();
        layoutRow4.setFlexGrow(1.0, layoutRow5);
        layoutRow5.addClassName(Gap.MEDIUM);
        layoutRow5.setWidth("100%");
        layoutRow5.getStyle().set("flex-grow", "1");
        layoutRow5.setAlignItems(Alignment.CENTER);
        layoutRow5.setJustifyContentMode(JustifyContentMode.END);
        h42.setText("Where to watch:");
        h42.setWidth("max-content");
        link.setText("https://www.Hbo.Max.com/movie-name");
        link.setHref("#");
        link.setWidth("min-content");
        layoutColumn3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth("100%");
        layoutColumn3.getStyle().set("flex-grow", "1");
        h3.setText("Cast");
        h3.setWidth("max-content");
        avatarItems.setWidth("min-content");
        setAvatarItemsSampleData(avatarItems);
        layoutColumn4.setWidthFull();
        getContent().setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setWidth("100%");
        layoutColumn4.getStyle().set("flex-grow", "1");
        h32.setText("Storyline");
        h32.setWidth("max-content");
        textMedium.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");

        getContent().add(layoutRow3);
        layoutRow3.add(badge);
        layoutRow3.add(badge2);
        getContent().add(layoutRow4);
        layoutRow4.add(buttonSecondary);
        layoutRow4.add(buttonSecondary2);
        layoutRow4.add(layoutRow5);
        layoutRow5.add(h42);
        layoutRow5.add(link);
        getContent().add(layoutColumn3);
        layoutColumn3.add(h3);
        layoutColumn3.add(avatarItems);
        getContent().add(layoutColumn4);
        layoutColumn4.add(h32);
        layoutColumn4.add(textMedium);
    }

    private void createUnderTitleRow() {
        HorizontalLayout underTitleRow = new HorizontalLayout();

        H2 releaseDateLabel = new H2();
        releaseDateLabel.setText("Release date:");
        releaseDateLabel.setWidth("max-content");
        underTitleRow.add(releaseDateLabel);

        createReleseDateText(underTitleRow, "20.20.2023");


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

        createRatingText(ratingNumberRow, "8.99");


        Icon ratingIcon = new Icon();
        ratingIcon.getElement().setAttribute("icon", "vaadin:star");
        ratingNumberRow.add(ratingIcon);

        Button rateButton = new Button();
        rateButton.setText("Rate");
        rateButton.setWidth("min-content");
        rateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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

    private void setAvatarItemsSampleData(MultiSelectListBox multiSelectListBox) {
        record Person(String name, String profession) {
        }
        ;
        List<Person> data = List.of(new Person("Aria Bailey", "Endocrinologist"), new Person("Aaliyah Butler", "Nephrologist"), new Person("Eleanor Price", "Ophthalmologist"), new Person("Allison Torres", "Allergist"), new Person("Madeline Lewis", "Gastroenterologist"));
        multiSelectListBox.setItems(data);
        multiSelectListBox.setRenderer(new ComponentRenderer(item -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(((Person) item).name);
            avatarItem.setDescription(((Person) item).profession);
            avatarItem.setAvatar(new Avatar(((Person) item).name));
            return avatarItem;
        }));
    }
}
