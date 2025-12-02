package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.dto.TmdbCastMemberDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.frontend.component.movie.CastMemberCard;
import com.clashofserres.cinematch.service.TmdbService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Movie Details")
@Route("movie/:id")
public class MovieDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final TmdbService tmdbService;
    private final VerticalLayout contentLayout = new VerticalLayout();

    public MovieDetailView(TmdbService tmdbService) {
        this.tmdbService = tmdbService;

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

        contentLayout.removeAll();

        // Header section with backdrop/poster
        Component header = createHeaderSection(movie);

        // Info section
        Component infoSection = createInfoSection(movie);

        // Cast section
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

        // Backdrop or poster image
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

            // Gradient overlay
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
            // Fallback background
            headerContainer.getStyle()
                    .set("background-color", "var(--lumo-primary-color-10pct)");
        }

        // Title overlay
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

        // Metadata row (release date, rating, runtime)
        HorizontalLayout metadataRow = new HorizontalLayout();
        metadataRow.setSpacing(true);
        metadataRow.getStyle().set("gap", "1.5rem");

        // Release date
        if (movie.releaseDate() != null && !movie.releaseDate().isEmpty()) {
            Span releaseDate = new Span(normalizeDate(movie.releaseDate()));
            releaseDate.getStyle()
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("color", "var(--lumo-secondary-text-color)");
            metadataRow.add(releaseDate);
        }

        // Rating
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

        // Runtime
        if (movie.runtime() != null && movie.runtime() > 0) {
            Span runtime = new Span(formatRuntime(movie.runtime()));
            runtime.getStyle()
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("color", "var(--lumo-secondary-text-color)");
            metadataRow.add(runtime);
        }

        infoLayout.add(metadataRow);

        // Genres
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

        // Plot summary
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
            return new Div(); // Return empty div if no cast
        }

        VerticalLayout castLayout = new VerticalLayout();
        castLayout.setPadding(true);
        castLayout.setSpacing(true);
        castLayout.setWidthFull();
        castLayout.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");

        H3 castTitle = new H3("Cast");

        // Horizontal scroller for cast
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

        // Show top 10 cast members
        List<TmdbCastMemberDTO> topCast = movie.credits().cast().stream()
                .limit(10)
                .toList();

        topCast.forEach(castMember -> {
            String name = castMember.name() != null ? castMember.name() : "Unknown";
            String character = castMember.character() != null ? castMember.character() : "Unknown Role";
            String profilePath = castMember.profilePath();

            castScroller.add(new CastMemberCard(name, character, profilePath));
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
}
