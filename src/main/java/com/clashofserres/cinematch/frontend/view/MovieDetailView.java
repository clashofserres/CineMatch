package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.dto.TmdbCastMemberDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.frontend.component.movie.CastMemberCard;
import com.clashofserres.cinematch.service.TmdbService;
import com.clashofserres.cinematch.service.WatchListService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@PageTitle("Movie Details")
@Route("movie/:id")
@AnonymousAllowed
public class MovieDetailView extends VerticalLayout implements BeforeEnterObserver {


    private final TmdbService tmdbService;
    private final WatchListService watchListService;
    private final VerticalLayout contentLayout = new VerticalLayout();
    private Button watchedButton;
    private boolean isMovieWatched = false;
    private long currentMovieId;


    @Autowired
    public MovieDetailView(TmdbService tmdbService, WatchListService watchListService) {
        this.tmdbService = tmdbService;
        this.watchListService = watchListService;

        setPadding(false);
        setSpacing(false);
        setSizeFull();

        contentLayout.setPadding(false);
        contentLayout.setSpacing(false);
        contentLayout.setWidthFull();

        add(contentLayout);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String idParam = event.getRouteParameters().get("id").orElse(null);

        if (idParam == null) {
            showError("Invalid movie ID");
            return;
        }

        try {
            long movieId = Long.parseLong(idParam);
            currentMovieId = movieId;
            loadMovieDetails(movieId);
        } catch (NumberFormatException e) {
            showError("Invalid movie ID format");
        } catch (TmdbService.TmdbServiceException e) {
            showError("Movie not found");
        } catch (Exception e) {
            showError("Failed to load movie details");
        }
    }


    private void loadMovieDetails(long movieId) {
        TmdbMovieDTO movie = tmdbService.getMovieDetails(movieId);


        try {
            isMovieWatched = watchListService.isInMyWatchList(movie);
        } catch (WatchListService.WatchListServiceFail e) {
            // Αν δεν έχει login/σφάλμα, απλά δεν είναι στο watchlist
            isMovieWatched = false;
        }



        contentLayout.removeAll();


        Component header = createHeaderSection(movie);


        Component infoSection = createInfoSection(movie);


        Component castSection = createCastSection(movie);

        contentLayout.add(header, infoSection, castSection);
    }

    private Component createHeaderSection(TmdbMovieDTO movie) {
        Div headerContainer = new Div();
        headerContainer.setWidthFull();
        headerContainer.getStyle()
                .set("position", "relative")
                .set("height", "400px")
                .set("overflow", "hidden");


        String imageUrl;
        if (movie.backdropPath() != null && !movie.backdropPath().isEmpty()) {
            imageUrl = "https://image.tmdb.org/t/p/w1280" + movie.backdropPath();
        } else if (movie.posterPath() != null && !movie.posterPath().isEmpty()) {
            imageUrl = "https://image.tmdb.org/t/p/w780" + movie.posterPath();
        } else {
            imageUrl = null;
        }

        if (imageUrl != null) {
            Image backdrop = new Image(imageUrl, movie.title());
            backdrop.setWidthFull();
            backdrop.getStyle()
                    .set("height", "400px")
                    .set("object-fit", "cover");


            Div overlay = new Div();
            overlay.getStyle()
                    .set("position", "absolute")
                    .set("top", "0")
                    .set("left", "0")
                    .set("width", "100%")
                    .set("height", "100%")
                    .set("background",
                            "linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.4) 50%, rgba(0,0,0,0.2) 100%)");

            headerContainer.add(backdrop, overlay);
        } else {

            headerContainer.getStyle()
                    .set("background-color", "var(--lumo-primary-color-10pct)");
        }


        Div titleContainer = new Div();
        titleContainer.getStyle()
                .set("position", "absolute")
                .set("bottom", "2rem")
                .set("left", "2rem")
                .set("right", "2rem")
                .set("z-index", "10");

        H1 title = new H1(movie.title() != null ? movie.title() : "Untitled");
        title.getStyle()
                .set("color", "white")
                .set("margin", "0")
                .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.8)");

        titleContainer.add(title);
        headerContainer.add(titleContainer);

        return headerContainer;
    }


    private Component createInfoSection(TmdbMovieDTO movie) {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(true);
        infoLayout.setWidthFull();
        infoLayout.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");


        HorizontalLayout kpiRow = new HorizontalLayout();
        kpiRow.setSpacing(true);
        kpiRow.getStyle().set("gap", "1rem");


        Component watchedBtn = createWatchedButton(movie);
        kpiRow.add(watchedBtn);



        if (movie.starPowerIndex() != null) {
            Span starPowerBadge = new Span(String.format("Star Power: %.1f", movie.starPowerIndex()));
            starPowerBadge.getElement().getThemeList().add("badge success"); // Πράσινο χρώμα
            starPowerBadge.getStyle()
                    .set("font-weight", "bold")
                    .set("font-size", "var(--lumo-font-size-s)");
            kpiRow.add(starPowerBadge);
        }


        if (movie.boxOfficeScore() != null) {
            Span boxOfficeBadge = new Span(String.format("Box Office Score: %.1f", movie.boxOfficeScore()));
            boxOfficeBadge.getElement().getThemeList().add("badge contrast"); // Γκρι/Μαύρο χρώμα
            boxOfficeBadge.getStyle()
                    .set("font-weight", "bold")
                    .set("font-size", "var(--lumo-font-size-s)");
            kpiRow.add(boxOfficeBadge);
        }

        infoLayout.add(kpiRow);


        HorizontalLayout metadataRow = new HorizontalLayout();
        metadataRow.setSpacing(true);
        metadataRow.getStyle().set("gap", "1.5rem");



        if (movie.releaseDate() != null && !movie.releaseDate().isEmpty()) {
            Span releaseDate = new Span(normalizeDate(movie.releaseDate()));
            releaseDate.getStyle()
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("color", "var(--lumo-secondary-text-color)");
            metadataRow.add(releaseDate);
        }


        if (movie.voteAverage() != null) {
            HorizontalLayout ratingLayout = new HorizontalLayout();
            ratingLayout.setSpacing(false);
            ratingLayout.setAlignItems(Alignment.CENTER);
            ratingLayout.getStyle().set("gap", "0.3rem");

            Icon starIcon = VaadinIcon.STAR.create();
            starIcon.setSize("18px");
            starIcon.getStyle().set("color", "#FFD700");

            Span rating = new Span(String.format("%.1f", movie.voteAverage()));
            rating.getStyle()
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("font-weight", "600");

            ratingLayout.add(starIcon, rating);
            metadataRow.add(ratingLayout);
        }


        if (movie.runtime() != null && movie.runtime() > 0) {
            Span runtime = new Span(formatRuntime(movie.runtime()));
            runtime.getStyle()
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("color", "var(--lumo-secondary-text-color)");
            metadataRow.add(runtime);
        }

        infoLayout.add(metadataRow);


        if (movie.genres() != null && !movie.genres().isEmpty()) {
            FlexLayout genresLayout = new FlexLayout();
            genresLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
            genresLayout.getStyle().set("gap", "0.5rem");

            movie.genres().forEach(genre -> {
                Span genreChip = new Span(genre.name());
                genreChip.getStyle()
                        .set("padding", "0.25rem 0.75rem")
                        .set("background-color", "var(--lumo-primary-color-10pct)")
                        .set("border-radius", "16px")
                        .set("font-size", "var(--lumo-font-size-s)")
                        .set("color", "var(--lumo-primary-text-color)");
                genresLayout.add(genreChip);
            });

            infoLayout.add(genresLayout);
        }


        if (movie.overview() != null && !movie.overview().isEmpty()) {
            H3 overviewTitle = new H3("Overview");
            overviewTitle.getStyle().set("margin-top", "1.5rem");

            Paragraph overview = new Paragraph(movie.overview());
            overview.getStyle()
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("line-height", "1.6")
                    .set("color", "var(--lumo-body-text-color)");

            infoLayout.add(overviewTitle, overview);
        }

        return infoLayout;
    }

    private Component createCastSection(TmdbMovieDTO movie) {

        if (movie.credits() == null || movie.credits().cast() == null || movie.credits().cast().isEmpty()) {
            return new Div();
        }

        VerticalLayout castLayout = new VerticalLayout();
        castLayout.setPadding(true);
        castLayout.setSpacing(true);
        castLayout.setWidthFull();
        castLayout.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");

        H3 castTitle = new H3("Cast");


        Div scrollerWrapper = new Div();
        scrollerWrapper.setWidthFull();
        scrollerWrapper.getStyle()
                .set("overflow-x", "auto")
                .set("overflow-y", "hidden");

        HorizontalLayout castScroller = new HorizontalLayout();
        castScroller.setSpacing(true);
        castScroller.setPadding(false);
        castScroller.getStyle()
                .set("gap", "1rem")
                .set("padding-bottom", "1rem");


        List<TmdbCastMemberDTO> topCast = movie.credits().cast().stream()
                .limit(10)
                .toList();

        topCast.forEach(castMember -> {
            String name = castMember.name() != null ? castMember.name() : "Unknown";
            String character = castMember.character() != null ? castMember.character() : "Unknown Role";
            String profilePath = castMember.profilePath();

            CastMemberCard card = new CastMemberCard(name, character, profilePath);
            castScroller.add(new Anchor("person/" + castMember.id(), card));
        });

        scrollerWrapper.add(castScroller);
        castLayout.add(castTitle, scrollerWrapper);

        return castLayout;
    }

    private void showError(String message) {

        contentLayout.removeAll();

        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setSizeFull();
        errorLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        errorLayout.setAlignItems(Alignment.CENTER);

        Icon errorIcon = LumoIcon.ERROR.create();
        errorIcon.setSize("64px");
        errorIcon.getStyle().set("color", "var(--lumo-error-color)");

        H2 errorText = new H2(message);
        errorText.getStyle().set("color", "var(--lumo-error-text-color)");

        errorLayout.add(errorIcon, errorText);
        contentLayout.add(errorLayout);
    }

    private String normalizeDate(String apiDate) {

        try {
            LocalDate date = LocalDate.parse(apiDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return apiDate;
        }
    }

    private String formatRuntime(int minutes) {

        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, mins);
        }
        return String.format("%dm", mins);
    }




    private Component createWatchedButton(TmdbMovieDTO movie) {
        Icon icon = VaadinIcon.EYE.create();

        watchedButton = new Button("Mark as Watched", icon, event -> {
            toggleWatchedStatus(movie); // Καλούμε τη λογική
        });

        watchedButton.getStyle().set("font-weight", "bold");


        updateWatchedButtonState();

        return watchedButton;
    }


    private void toggleWatchedStatus(TmdbMovieDTO movie) {
        try {
            if (isMovieWatched) {
                watchListService.removeFromWatchList(movie);
                isMovieWatched = false;
                Notification.show("Removed from Watchlist!", 3000, Notification.Position.BOTTOM_END);
            } else {
                watchListService.addToWatchList(movie);
                isMovieWatched = true;
                Notification.show("Added to Watchlist!", 3000, Notification.Position.BOTTOM_END);
            }
            updateWatchedButtonState();
        } catch (WatchListService.WatchListServiceFail e) {

            Notification.show(e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }


    private void updateWatchedButtonState() {
        if (watchedButton == null) return;

        if (isMovieWatched) {
            watchedButton.setText("Watched");
            watchedButton.getStyle().set("background-color", "var(--lumo-success-color)");
            watchedButton.getStyle().set("color", "white");
        } else {
            watchedButton.setText("Mark as Watched");
            watchedButton.getStyle().set("background-color", "var(--lumo-contrast-50pct)");
            watchedButton.getStyle().set("color", "var(--lumo-primary-text-color)");
        }
    }
}
