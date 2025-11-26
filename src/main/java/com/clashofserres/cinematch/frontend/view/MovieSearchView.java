package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.config.TmdbConfig;
import com.clashofserres.cinematch.frontend.component.movie.MovieCard;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@PageTitle("Movie Search")
@Route("movies")
@Menu(order = 1, icon = LineAwesomeIconUrl.SEARCH_SOLID, title = "Movie Search")
public class MovieSearchView extends VerticalLayout {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final FlexLayout movieResults = new FlexLayout();
	private final VerticalLayout movieHolder = new VerticalLayout();
	private final H2 whatsHotText = new H2("See what's hot right now 🔥");

	// TODO: REMOVE
	private final TmdbConfig tmdbConfig;

	public MovieSearchView(TmdbConfig tmdbConfig) {
		// TODO: REMOVE
		this.tmdbConfig = tmdbConfig;

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

	// TODO Rework
	private void searchMovies(String query) {
		String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
		String url = "https://api.themoviedb.org/3/search/movie?api_key=" + tmdbConfig.getKey() +
				"&include_adult=false&query=" + encodedQuery;

		whatsHotText.setVisible(false);
		renderMovieList(url);
	}

	// TODO Rework
	private void loadPopularMovies() {
		String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + tmdbConfig.getKey();

		whatsHotText.setVisible(true);
		renderMovieList(url);
	}

	// TODO Rework
	private void renderMovieList(String url) {
		try {
			JsonNode json = fetchJson(url);

			movieResults.removeAll();

			JsonNode results = json.get("results");
			if (!results.isArray() || results.isEmpty()) {
				movieResults.removeAll();
				movieResults.add(new Div("No results found.."));
				return;
			}
			results.forEach(movie -> {
				String title = movie.get("title").asText();
				String poster = movie.get("poster_path").isNull() ? null : movie.get("poster_path").asText();
				String release = movie.hasNonNull("release_date") ? movie.get("release_date").asText() : "Unknown";
				double rating = movie.hasNonNull("vote_average") ? movie.get("vote_average").asDouble() : 0;

				movieResults.add(new MovieCard(title, poster, release, rating));
			});

		} catch (Exception e) {
			movieResults.removeAll();
			movieResults.add(new Div("Error: " + e.getMessage()));
		}
	}
	// TODO Rework
	private JsonNode fetchJson(String url) throws Exception {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();

		HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

		return MAPPER.readTree(response.body());
	}
}
