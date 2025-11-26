package com.clashofserres.cinematch.frontend.component.movie;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MovieCard extends VerticalLayout
{

    public MovieCard(String title, String posterPath, String releaseDate, double rating)
    {
        setPadding(false);
        setSpacing(false);
        setWidth("160px");
        getStyle().set("align-items", "flex-start");

        int ratingPercent = (int) Math.round(rating * 10);

        // CARD that contains poster + rating bar
        Card posterCard = new Card();
        posterCard.getStyle()
                .set("padding", "0")
                .set("border-radius", "8px");
                //.set("overflow", "hidden")        // ensures content fills card shape
               // .set("aspect-ratio", "2 / 3");    // fixed poster ratio

        posterCard.setWidth("160px");
        posterCard.setHeight("auto");

        posterCard.setMedia(createPosterMedia(posterPath, title));


        // TEXT BELOW CARD -------------------------------------------------
        Span titleLabel = new Span(title);
        titleLabel.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-top", "0.3rem");

        Span dateLabel = new Span(normalizeDate(releaseDate));
        dateLabel.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)");

        add(posterCard, titleLabel, dateLabel);
    }

    private Component createPosterMedia(String posterPath, String title)
    {
        if (posterPath != null)
        {
            Image image = new Image("https://image.tmdb.org/t/p/w300" + posterPath, title);
            image.setWidth("100%");
            image.getStyle().set("border-radius", "8px");
            return image;
        }

        // FALLBACK WRAPPER SAME SIZE AS IMAGE
        Div placeholder = new Div();
        placeholder.setWidth("100%");
        placeholder.setHeight("240px"); // same as typical 300px width 2:3 ratio
        placeholder.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background-color", "var(--lumo-primary-color-10pct)")
                .set("border-radius", "8px");

        Icon icon = LumoIcon.PHOTO.create();
        icon.setSize("48px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");

        placeholder.add(icon);
        return placeholder;
    }


    private String normalizeDate(String apiDate)
    {
        try
        {
            LocalDate date = LocalDate.parse(apiDate); // ISO format works directly

            // format to "MMMM dd, yyyy"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            return date.format(formatter);
        }
        catch (Exception e)
        {
            return apiDate;
        }
    }

}
