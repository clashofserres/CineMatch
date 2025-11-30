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

        // 1. Calculate percentage for the filename
        int percentage = (int) Math.round(rating * 10);

        Card posterCard = new Card();
        posterCard.addClassName("offset-hovered-element");
        posterCard.getStyle()
                .set("padding", "0")
                .set("border-radius", "8px")
                .set("overflow", "visible");

        posterCard.setWidth("160px");
        posterCard.setHeight("auto");

        // --- WRAPPER FOR OVERLAY LOGIC ---
        Div imageContainer = new Div();
        imageContainer.setWidth("100%");
        imageContainer.getStyle().set("position", "relative"); // Essential for absolute positioning of children

        // A. The Poster
        Component poster = createPosterMedia(posterPath, title);

        // B. The Rating Image (Requested Line)
        Image ratingImg = new Image("images/progress_icons/progress_" + percentage + ".svg", percentage + "%");

        // C. Style the Rating Image to float on top
        ratingImg.setWidth("38px");  // Adjust size as needed
        ratingImg.setHeight("38px");
        ratingImg.getStyle()
                .set("position", "absolute")
                .set("bottom", "-10px")      // Negative value makes it hang slightly off the bottom edge
                .set("left", "10px")         // Distance from left
                .set("background-color", "#081c22") // Dark background (TMDB style) to make it readable
                .set("border-radius", "50%") // Make background circular
                .set("padding", "2px")       // Slight padding inside the dark circle
                .set("z-index", "2");        // Ensure it sits on top of the poster

        // Add both to the wrapper
        imageContainer.add(poster, ratingImg);

        // Add wrapper to the card
        posterCard.add(imageContainer);

        // TEXT BELOW CARD -------------------------------------------------
        Span titleLabel = new Span(title);
        titleLabel.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-top", "1rem"); // Increased margin slightly to account for the overlapping icon

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
            image.getStyle()
                    .set("border-radius", "8px")
                    .set("display", "block"); // Removes bottom phantom spacing in some browsers
            return image;
        }

        // FALLBACK WRAPPER
        Div placeholder = new Div();
        placeholder.setWidth("100%");
        placeholder.setHeight("240px");
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
        try {
            LocalDate date = LocalDate.parse(apiDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return apiDate;
        }
    }
}
