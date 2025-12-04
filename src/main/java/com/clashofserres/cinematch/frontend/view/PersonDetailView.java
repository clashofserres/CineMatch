package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.controller.PersonController;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbPersonProfileDTO;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.http.ResponseEntity;
import com.vaadin.flow.component.Component;

@Route("person")
public class PersonDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final PersonController personController;
    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200";

    public PersonDetailView(PersonController personController) {
        this.personController = personController;
        add(new H1("Loading Profile..."));
    }

    @Override
    public void setParameter(BeforeEvent event, Long personId) {
        loadPersonDetails(personId);
    }

    private void loadPersonDetails(Long personId) {
        try {
            ResponseEntity<TmdbPersonProfileDTO> response = personController.getPersonProfile(personId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                displayProfile(response.getBody());
            } else {
                displayError("Profile not found or internal error.");
            }

        } catch (Exception e) {
            displayError("Could not fetch data for ID: " + personId);
        }
    }

    private void displayProfile(TmdbPersonProfileDTO profile) {
        removeAll();


        H1 name = new H1(profile.name());
        Image profilePic = new Image("https://image.tmdb.org/t/p/w300" + profile.profilePath(), profile.name() + " Picture");
        profilePic.getStyle().set("border-radius", "8px");

        Div bioDetails = new Div();
        bioDetails.add(new Div("Birthday: " + profile.birthday()));
        bioDetails.add(new Div("Known For: " + profile.knownForDepartment()));

        Div biography = new Div(profile.biography());
        biography.getStyle().set("margin-top", "15px");


        H2 filmographyHeader = new H2("Filmography / Known For");
        FlexLayout filmographyLayout = new FlexLayout();
        filmographyLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filmographyLayout.getStyle().set("gap", "15px");

        if (profile.filmography() != null && !profile.filmography().isEmpty()) {
            profile.filmography().forEach(movie -> {

                filmographyLayout.add(createFilmographyCard(movie));
            });
        } else {
            filmographyLayout.add(new Div("No major credits found."));
        }


        add(name, profilePic);
        add(bioDetails, biography);
        add(filmographyHeader, filmographyLayout);
    }


    private Component createFilmographyCard(TmdbMovieDTO movie) {


        String title = movie.title() != null ? movie.title() : "Unnamed Item";
        String posterPath = movie.posterPath();

        VerticalLayout cardLayout = new VerticalLayout();
        cardLayout.setSpacing(false);
        cardLayout.setPadding(false);
        cardLayout.setWidth("120px");


        Image posterImage;
        if (posterPath != null) {
            String fullImageUrl = TMDB_IMAGE_BASE_URL + posterPath;
            posterImage = new Image(fullImageUrl, title);
            posterImage.setWidth("100%");
            posterImage.getStyle().set("border-radius", "4px");
        } else {

            posterImage = new Image("", "No Poster Available");
            posterImage.setWidth("100%");
            posterImage.setHeight("180px");
            posterImage.getStyle()
                    .set("background-color", "var(--lumo-contrast-10pct)")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center");
        }


        Div titleDiv = new Div(title);
        titleDiv.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("text-align", "center")
                .set("padding-top", "4px");

        cardLayout.add(posterImage, titleDiv);


        return new Anchor("movie/" + movie.id(), cardLayout);
    }


    private void displayError(String message) {
        removeAll();
        add(new H1("Error: " + message));
    }
}