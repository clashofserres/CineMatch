package com.clashofserres.cinematch.frontend.component.movie;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ActorCard extends VerticalLayout {

    public ActorCard(String name, String profilePath) {
        setPadding(false);
        setSpacing(false);
        setWidth("160px");
        getStyle().set("align-items", "flex-start");

        // Create a card for the actor
        Card actorCard = new Card();
        actorCard.addClassName("offset-hovered-element");
        actorCard.getStyle()
                .set("padding", "0")
                .set("border-radius", "8px")
                .set("overflow", "visible");

        actorCard.setWidth("160px");
        actorCard.setHeight("auto");

        // --- WRAPPER FOR IMAGE LOGIC ---
        Div imageContainer = new Div();
        imageContainer.setWidth("100%");
        imageContainer.getStyle().set("position", "relative"); // Essential for absolute positioning of children

        // A. The Actor's Profile Image
        Component profileImage = createProfileImage(profilePath, name);

        // Add the profile image container to the card
        imageContainer.add(profileImage);

        // Add image container to the card
        actorCard.add(imageContainer);

        // TEXT BELOW CARD -------------------------------------------------
        Span nameLabel = new Span(name);
        nameLabel.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-top", "1rem"); // Increased margin slightly to account for the overlapping text

        // Add nameLabel below the card (or you can add more text)
        add(actorCard, nameLabel);
    }

    private Component createProfileImage(String profilePath, String name) {
        if (profilePath != null && !profilePath.isEmpty()) {
            // Construct the full URL to the actor's profile image
            String imageUrl = "https://image.tmdb.org/t/p/w300" + profilePath;

            Image image = new Image(imageUrl, name);
            image.setWidth("100%");
            image.getStyle()
                    .set("border-radius", "8px")
                    .set("display", "block");
            return image;
        }

        // FALLBACK WRAPPER IF IMAGE IS MISSING
        Div placeholder = new Div();
        placeholder.setWidth("100%");
        placeholder.setHeight("240px");
        placeholder.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background-color", "var(--lumo-primary-color-10pct)")
                .set("border-radius", "8px");

        // Add a fallback icon when there is no image
        Icon icon = new Icon("vaadin:photo"); // Using Vaadin's built-in photo icon as a fallback
        icon.setSize("48px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");

        placeholder.add(icon);
        return placeholder;
    }
}
