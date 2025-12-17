package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.frontend.component.movie.MovieCard;
import com.clashofserres.cinematch.frontend.core.MainLayout;
import com.clashofserres.cinematch.service.RecommendationService;
import com.clashofserres.cinematch.service.TmdbService;
import com.clashofserres.cinematch.service.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.Optional;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class) // Η Αρχική Σελίδα
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID, title = "Home") // Εικονίδιο Σπιτιού
@AnonymousAllowed
public class CineMatchTestView extends VerticalLayout {

    private final RecommendationService recommendationService;
    private final TmdbService tmdbService;
    private final UserService userService;

    public CineMatchTestView(RecommendationService recommendationService, TmdbService tmdbService, UserService userService) {
        this.recommendationService = recommendationService;
        this.tmdbService = tmdbService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Welcome to CineMatch 🍿"));

        createRecommendationSection();
    }

    private void createRecommendationSection() {
        Optional<UserEntity> currentUser = userService.getMyUserOptional();
        List<TmdbMovieDTO> moviesToShow;
        String sectionTitle;

        if (currentUser.isPresent()) {
            // ΣΕΝΑΡΙΟ 1: Συνδεδεμένος Χρήστης -> Προσωποποιημένες Προτάσεις
            moviesToShow = recommendationService.getRecommendationsForUser(currentUser.get());
            sectionTitle = "Recommended for You (Based on your Watchlist) 🔥";
        } else {
            // ΣΕΝΑΡΙΟ 2: Επισκέπτης -> Δημοφιλείς Ταινίες
            moviesToShow = tmdbService.getPopularMovies().results();
            sectionTitle = "Popular Movies ⭐";
        }

        add(new H2(sectionTitle));

        if (moviesToShow.isEmpty()) {
            add(new Paragraph("Add movies to your watchlist to get personalized recommendations!"));
        } else {
            // Grid με κάρτες ταινιών
            FlexLayout movieContainer = new FlexLayout();
            movieContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
            movieContainer.getStyle().set("gap", "18px");
            movieContainer.setJustifyContentMode(JustifyContentMode.START);
            movieContainer.setWidthFull();

            for (TmdbMovieDTO movie : moviesToShow) {
                // Ελέγχουμε τα null για να μην σκάσει
                String title = movie.title() != null ? movie.title() : "Untitled";
                String poster = movie.posterPath();
                String release = movie.releaseDate() != null ? movie.releaseDate() : "Unknown";
                double rating = movie.voteAverage() != null ? movie.voteAverage() : 0.0;

                // Χρησιμοποιούμε το ΥΠΑΡΧΟΝ MovieCard σου!
                movieContainer.add(new MovieCard(movie.id(), title, poster, release, rating));
            }

            add(movieContainer);
        }
    }
}