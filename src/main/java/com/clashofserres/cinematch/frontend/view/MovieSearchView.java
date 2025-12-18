package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
import com.clashofserres.cinematch.frontend.component.movie.MovieCard;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.Component;

import java.util.List;

@PageTitle("Movie Search")
@Route("movies")
@Menu(order = 1, icon = LineAwesomeIconUrl.SEARCH_SOLID, title = "Movie Search")
@AnonymousAllowed
public class MovieSearchView extends VerticalLayout {

	private final TmdbService tmdbService;
	private Icon spinner;
	private final FlexLayout movieResults = new FlexLayout();
    private final H2 whatsHotText = new H2("See what's hot right now 🔥");

	public MovieSearchView(TmdbService tmdbService) {
		this.tmdbService = tmdbService;

		setPadding(true);
		setSpacing(true);

		TextField searchField = new TextField();
		searchField.setPlaceholder("Search movies...");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setWidthFull();
		searchField.setValueChangeMode(ValueChangeMode.EAGER);

		HorizontalLayout searchBar = new HorizontalLayout(searchField);
		searchBar.setWidthFull();
		searchBar.setAlignItems(Alignment.END);

		spinner = VaadinIcon.REFRESH.create();
		spinner.addClassName("spinning-icon");
		spinner.setVisible(false);
		spinner.getStyle().set("color", "#ffab00");
		spinner.getStyle().set("font-size", "var(--lumo-size-xl)");

		movieResults.setFlexWrap(FlexLayout.FlexWrap.WRAP);
		movieResults.getStyle().set("gap", "18px");
		movieResults.setWidthFull();


		VerticalLayout movieHolder = new VerticalLayout();
		movieHolder.setAlignItems(Alignment.CENTER);
		movieHolder.add(whatsHotText, spinner, movieResults);
		movieHolder.setWidthFull();

		add(searchBar, movieHolder);

		searchField.addValueChangeListener(evt -> {
			String query = evt.getValue();
			if (query != null && query.length() > 1) {
				searchMovies(query);
			} else {
				loadPopularMovies();
			}
		});

		loadPopularMovies();
	}


	private void searchMovies(String query) {
		whatsHotText.setVisible(false);
		movieResults.removeAll();
		movieResults.setVisible(false);
		spinner.setVisible(true);


		TmdbMovieListResponseDTO response = tmdbService.searchMovies(query);
		renderMovies(response.results());

		spinner.setVisible(false);
		movieResults.setVisible(true);
	}

	private void loadPopularMovies() {
		whatsHotText.setVisible(true);
		movieResults.setVisible(false);
		spinner.setVisible(true);


		TmdbMovieListResponseDTO response = tmdbService.getPopularMovies();
		renderMovies(response.results());

		spinner.setVisible(false);
		movieResults.setVisible(true);
	}

	private void renderMovies(List<TmdbMovieDTO> movies) {
		movieResults.removeAll();

		if (movies == null || movies.isEmpty()) {
			movieResults.add(new Div("No results found.."));
			return;
		}

		movies.forEach(movie -> {
			String title = movie.title() != null ? movie.title() : "Untitled";
			String poster = movie.posterPath();
			String release = movie.releaseDate() != null ? movie.releaseDate() : "Unknown";
			double rating = movie.voteAverage() != null ? movie.voteAverage() : 0.0;

			movieResults.add(new MovieCard(movie.id(), title, poster, release, rating));
		});
	}
}