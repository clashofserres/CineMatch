package com.clashofserres.cinematch.frontend.component.movie;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

public class CastMemberCard extends VerticalLayout {

    public CastMemberCard(String name, String character, String profilePath) {
        setPadding(false);
        setSpacing(false);
        setWidth("120px");
        getStyle().set("align-items", "flex-start");

        // Profile photo
        Component profilePhoto = createProfilePhoto(profilePath, name);
        profilePhoto.getElement().getStyle()
                .set("width", "120px")
                .set("height", "120px")
                .set("border-radius", "8px")
                .set("object-fit", "cover");

        // Actor name
        Span nameLabel = new Span(name);
        nameLabel.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-top", "0.5rem")
                .set("width", "120px")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        // Character name
        Span characterLabel = new Span(character);
        characterLabel.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("width", "120px")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        add(profilePhoto, nameLabel, characterLabel);
    }

    private Component createProfilePhoto(String profilePath, String name) {
        if (profilePath != null && !profilePath.isEmpty()) {
            Image image = new Image("https://image.tmdb.org/t/p/w185" + profilePath, name);
            image.setWidth("120px");
            image.setHeight("120px");
            image.getStyle()
                    .set("border-radius", "8px")
                    .set("object-fit", "cover");
            return image;
        }

        // Fallback for missing profile photo
        Div placeholder = new Div();
        placeholder.setWidth("120px");
        placeholder.setHeight("120px");
        placeholder.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background-color", "var(--lumo-primary-color-10pct)")
                .set("border-radius", "8px");

        Icon icon = LumoIcon.USER.create();
        icon.setSize("48px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");

        placeholder.add(icon);
        return placeholder;
    }
}
