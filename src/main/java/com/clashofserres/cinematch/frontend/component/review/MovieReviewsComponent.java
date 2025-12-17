package com.clashofserres.cinematch.frontend.component.review;

import com.clashofserres.cinematch.data.model.ReviewEntity;
import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.frontend.view.MovieDetailView;
import com.clashofserres.cinematch.repository.ReviewRepository;
import com.clashofserres.cinematch.service.ReviewService;
import com.clashofserres.cinematch.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// Use @UIScope to tie the component instance to the user session (UI)
@SpringComponent
@UIScope
public class MovieReviewsComponent extends VerticalLayout {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private Long movieId; // The ID of the movie whose reviews we are displaying
    private VerticalLayout reviewsContainer; // Layout to hold all the review items

    Optional<UserEntity> userEntity = Optional.empty();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");

    public MovieReviewsComponent(ReviewService reviewService, UserService userService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.userService = userService;

        this.setSpacing(true);
        this.setPadding(false);
        this.setWidthFull();

        this.add(new Div(new Span("No reviews yet. Be the first to review!")));
        this.reviewRepository = reviewRepository;
    }

    public void loadMyUser() {
        userEntity = userService.getMyUserOptional();
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
        refreshReviews();
    }

    public void refreshReviews() {
        loadReviews(movieId);
        loadMyUser();
    }

    public void loadMyReviews() {
        loadMyUser();

        try {
            var myUser = userService.getMyUserOptional();
            if (myUser.isEmpty()) {
                displayFail("No user found.");
                return;
            }

            List<ReviewEntity> reviews = reviewService.findByUserId(myUser.get().getId());
            if (reviews.isEmpty()) {
                displayFail("No reviews yet.");
                return;
            }

            this.removeAll();


            reviews.stream()
                    .map(review -> createReviewItem(
                            review, myUser.get(), true))
                    .forEach(this::add);

        }
        catch (Exception e) {
            displayFail(e.getMessage());
        }
    }

    public void loadReviews(Long movieId) {
        if (movieId == null) {
            displayFail("No movie selected.");
            return;
        }

        try {
            List<ReviewEntity> reviews = reviewService.findByMovieIdSorted(movieId);
            this.removeAll();

            if (reviews.isEmpty()) {
                displayFail("No reviews yet. Be the first to review!");
                return;
            }

            Set<Long> userIds = reviews.stream()
                    .map(ReviewEntity::getUserId)
                    .collect(Collectors.toSet());

            Map<Long, UserEntity> usersById = userService.findUsersByIdIsIn(userIds).stream()
                    .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

            reviews.stream()
                    .map(review -> createReviewItem(
                            review, usersById.get(review.getUserId()), false))
                    .forEach(this::add);

        } catch (Exception e) {
            displayFail(e.getMessage());
        }
    }

    void displayFail(String message) {
        this.removeAll();
        this.add(new Div(new Span("Error loading reviews: " + message)));
    }

    private Component createReviewItem(ReviewEntity review, UserEntity user, boolean isUserReview) {
        String username = user != null ? user.getUsername() : "Unknown User";

        HorizontalLayout userHeader = new HorizontalLayout();
        userHeader.setAlignItems(Alignment.CENTER);
        userHeader.setSpacing(true);

        Span timestampSpan = new Span(review.getTimestamp().format(DATE_TIME_FORMATTER));
        timestampSpan.getElement().getThemeList().add("badge tertiary"); // Use Vaadin theme for subtle look

        if (isUserReview) {

            Anchor movieAnchor = new Anchor("movie/" + review.getMovieId(), review.getMovieTitle());

            userHeader.add(movieAnchor, timestampSpan);
        }
        else {
            Avatar avatar = new Avatar(username);
            avatar.setColorIndex(Math.toIntExact(user.getId()));
            Span usernameSpan = new Span(username);
            userHeader.add(avatar, usernameSpan, timestampSpan);
        }

        Div content = new Div(new Span(review.getContent()));
        content.getStyle().set("padding-left", "10px");
        content.setWidthFull();
        content.setMaxWidth(600, Unit.PIXELS); //Limit review width for readability

         if (review.getReviewSentiment() != null) {
            String sentiment = review.getReviewSentiment().toUpperCase();
            Span sentimentBadge = new Span();
            switch (sentiment) {
                case "POSITIVE": {
                    sentimentBadge.setText("Positive 😄");
                    sentimentBadge.getElement().getThemeList().add("badge success");
                    userHeader.add(sentimentBadge);
                    break;
                }
                case "NEGATIVE": {
                    sentimentBadge.setText("Negative ☹️");
                    sentimentBadge.getElement().getThemeList().add("badge error");
                    userHeader.add(sentimentBadge);
                    break;
                }
            };


        }

        VerticalLayout reviewLayout = new VerticalLayout(userHeader, content);
        reviewLayout.setPadding(true);
        reviewLayout.setSpacing(true);
        reviewLayout.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("padding-bottom", "var(--lumo-space-m)");

        attachContextMenu(reviewLayout, review, isUserReview);

        return reviewLayout;
    }

    void attachContextMenu(VerticalLayout reviewLayout, ReviewEntity review, boolean isUserReview) {
        boolean shouldAttach = false;

        ContextMenu menu = new ContextMenu();

        if (isUserReview) {
            shouldAttach = true;
            menu.addItem("View Movie", menuItemClickEvent -> {
                UI.getCurrent().navigate("movie/" + review.getMovieId());
            });
        }

        if (userEntity.isPresent() && userEntity.get().getId().equals(review.getUserId())) {
            shouldAttach = true;

            Span deleteSpan = new Span("Delete");
            deleteSpan.getStyle().set("color", "var(--lumo-error-text-color)");
            menu.addItem(deleteSpan, menuItemClickEvent -> {
                try {
                    reviewRepository.delete(review);
                    postDeleteRefresh(isUserReview);
                }
                catch (ReviewService.ReviewFailedException e) {
                    displayFail(e.getMessage());
                }
                catch (Exception e) {
                    displayFail(e.getMessage());
                }
            });
        }

        if (shouldAttach) {
            menu.setTarget(reviewLayout);
        }
    }

    private void postDeleteRefresh(boolean isUserReview) {
        if (isUserReview) {
            loadMyReviews();
        }
        else {
            refreshReviews();
        }
    }
}