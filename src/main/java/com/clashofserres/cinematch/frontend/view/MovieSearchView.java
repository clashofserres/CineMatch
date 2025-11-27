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
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Movie Search")
@Route("movies")
@Menu(order = 1, icon = LineAwesomeIconUrl.SEARCH_SOLID, title = "Movie Search")
public class MovieSearchView extends VerticalLayout {

	private final TmdbService tmdbService;

	private final FlexLayout movieResults = new FlexLayout();
	private final VerticalLayout movieHolder = new VerticalLayout();
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

		movieResults.setFlexWrap(FlexLayout.FlexWrap.WRAP);
		movieResults.getStyle().set("gap", "18px");
		movieResults.setWidthFull();

		movieHolder.add(whatsHotText, movieResults);

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
		TmdbMovieListResponseDTO response = tmdbService.searchMovies(query);
		renderMovies(response.results());
	}

	private void loadPopularMovies() {
		whatsHotText.setVisible(true);
		TmdbMovieListResponseDTO response = tmdbService.getPopularMovies();
		renderMovies(response.results());
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

			movieResults.add(new MovieCard(title, poster, release, rating));
		});
	}
}