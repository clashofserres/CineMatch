package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.dto.TmdbPersonDTO;
import com.clashofserres.cinematch.data.dto.TmdbPersonListResponseDTO;
import com.clashofserres.cinematch.frontend.component.movie.ActorCard;
import com.clashofserres.cinematch.service.TmdbService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import com.vaadin.flow.component.html.Anchor;

import java.util.List;

@PageTitle("Person Search")
@Route("Persons")
@Menu(order = 1, icon = LineAwesomeIconUrl.SEARCH_SOLID, title = "Person Search")
public class ActorSearchView extends VerticalLayout {

    private final TmdbService tmdbService;

    private final FlexLayout actorResults = new FlexLayout();
    private final VerticalLayout actorHolder = new VerticalLayout();
    private final H2 whatsHotText = new H2("See top Persons right now 🎭");

    public ActorSearchView(TmdbService tmdbService) {
        this.tmdbService = tmdbService;

        setPadding(true);
        setSpacing(true);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Persons...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidthFull();
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        HorizontalLayout searchBar = new HorizontalLayout(searchField);
        searchBar.setWidthFull();
        searchBar.setAlignItems(Alignment.END);

        actorResults.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        actorResults.getStyle().set("gap", "18px");
        actorResults.setWidthFull();

        actorHolder.add(whatsHotText, actorResults);
        add(searchBar, actorHolder);

        searchField.addValueChangeListener(evt -> {
            String query = evt.getValue();
            if (query != null && query.length() > 1) {
                searchActors(query);
            } else {
                loadPopularActors();
            }
        });

        loadPopularActors();
    }

    private void searchActors(String query) {
        whatsHotText.setVisible(false);
        TmdbPersonListResponseDTO response = tmdbService.searchPeople(query);
        renderActors(response.results());
    }

    private void loadPopularActors() {
        whatsHotText.setVisible(true);
        TmdbPersonListResponseDTO response = tmdbService.getPopularPeople();
        renderActors(response.results());
    }

    private void renderActors(List<TmdbPersonDTO> actors) {
        actorResults.removeAll();

        if (actors == null || actors.isEmpty()) {
            actorResults.add(new Div("No results found.."));
            return;
        }

        actors.forEach(actor -> {

            Long personId = actor.id();
            String name = actor.name() != null ? actor.name() : "Unknown Person";
            String profilePath = actor.profilePath();
            String knownFor = actor.knownForDepartment() != null ? actor.knownForDepartment() : "Unknown";


            ActorCard card = new ActorCard(name, profilePath);


            Anchor personLink = new Anchor("person/" + personId, card);

            actorResults.add(personLink);
        });
    }
}
