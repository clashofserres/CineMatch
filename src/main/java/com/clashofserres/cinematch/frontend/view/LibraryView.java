package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.frontend.component.movie.MovieCard;
import com.clashofserres.cinematch.service.WatchListService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.Collections;
import java.util.List;

@PageTitle("My Library")
@Route("library")
@AnonymousAllowed
public class LibraryView extends VerticalLayout {

    private final WatchListService watchListService;
    private final FlexLayout movieGrid = new FlexLayout();


    @Autowired
    public LibraryView(WatchListService watchListService) {
        this.watchListService = watchListService;

        getStyle().set("padding", "var(--lumo-space-l)");
        getStyle().set("gap", "var(--lumo-space-m)");

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        add(new H1("My Watchlist"));

        setupMovieGrid();
        loadWatchedMovies();
    }

    private void setupMovieGrid() {
        movieGrid.setFlexWrap(FlexWrap.WRAP);


        movieGrid.getStyle().set("gap", "20px");

        movieGrid.setWidth("1200px");
        movieGrid.getStyle().set("margin-top", "20px");
        add(movieGrid);
    }
    private void loadWatchedMovies() {
        movieGrid.removeAll();

        List<TmdbMovieDTO> watchedMovies;

        try {

            watchedMovies = watchListService.getWatchList();

        } catch (Exception e) {

            add(new H3("Please log in to view your Watchlist or an error occurred."));
            return;
        }

        if (watchedMovies == null || watchedMovies.isEmpty()) {
            add(new H3("Your Watchlist is empty. Start adding movies!"));
            return;
        }

        watchedMovies.forEach(movie -> {
            MovieCard card = new MovieCard(
                    movie.id(),
                    movie.title(),
                    movie.posterPath(),
                    movie.releaseDate(),
                    movie.voteAverage()
            );
            movieGrid.add(card);
        });
    }
}