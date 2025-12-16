package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.model.ReviewEntity;
import com.clashofserres.cinematch.repository.ReviewRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final HuggingFaceService huggingFaceService;

    public static class ReviewFailedException extends RuntimeException {
        public ReviewFailedException(String message) { super(message); }
    }

    public ReviewService(UserService userService, ReviewRepository reviewRepository, HuggingFaceService huggingFaceService) {
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.huggingFaceService = huggingFaceService;
    }

    public List<ReviewEntity> findMyReviews() {
        var user = userService.getMyUserOptional();
        if (!user.isPresent()) {
            throw new ReviewFailedException("No user logged in!");
        }
        return findByUserId(user.get().getId());
    }
    public List<ReviewEntity> findByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }

    public List<ReviewEntity> findByMovieId(Long movieId) {
        return reviewRepository.findALlByMovieId(movieId);
    }

    public List<ReviewEntity> findByMovieIdSorted(Long movieId) {
        List<ReviewEntity> reviews = findByMovieId(movieId);

        var user = userService.getMyUserOptional();
        if (user.isPresent()) {
            final Long userId = user.get().getId();

            Comparator<ReviewEntity> userFirstComparator = (r1, r2) -> {
                boolean isR1User = r1.getUserId().equals(userId);
                boolean isR2User = r2.getUserId().equals(userId);

                // Priority 1: User's review always comes first
                if (isR1User && !isR2User) {
                    return -1; // r1 comes before r2
                }
                if (!isR1User && isR2User) {
                    return 1;  // r2 comes before r1 (so r1 comes after r2)
                }
                return r2.getTimestamp().compareTo(r1.getTimestamp());
            };

            Collections.sort(reviews, userFirstComparator);
        }

        return reviews;
    }

    public void writeReview(TmdbMovieDTO movieDTO, String content) throws ReviewFailedException{
        writeReview(movieDTO.id(), movieDTO.title(), content);
    }

    public void writeReview(Long movieId, String movieTitle, String content) throws ReviewFailedException {
        var user = userService.getMyUserOptional();
        if (!user.isPresent()) {
            throw new ReviewFailedException("No user logged in!");
        }

        if (content.isEmpty()) {
            throw new ReviewFailedException("Review content cannot be empty!");
        }

        try {
            ReviewEntity newReview = new ReviewEntity(user.get().getId());
            newReview.setMovieId(movieId);
            newReview.setMovieTitle(movieTitle);
            newReview.setContent(content);

            // Save the review first
            reviewRepository.save(newReview);

            addReviewSentiment(newReview);

        }
        catch (Exception e) {
            throw new ReviewFailedException(e.getMessage());
        }
    }

    public void deleteReview(Long reviewId) throws ReviewFailedException {
        var review = reviewRepository.findById(reviewId);
        if (!review.isPresent()) {
            throw new ReviewFailedException("Review not found!");
        }
        deleteReview(review.get());
    }

    public void deleteReview(ReviewEntity review) throws ReviewFailedException {
        var user = userService.getMyUserOptional();
        if (!user.isPresent()) {
            throw new ReviewFailedException("No user logged in!");
        }
        if (!review.getUserId().equals(user.get().getId())) {
            throw new ReviewFailedException("You do not have permission to delete this review!");
        }
        reviewRepository.delete(review);
    }

    // This method will now run in a separate thread.
    @Async
    protected void addReviewSentiment(ReviewEntity entity) {
        String prompt = buildSentimentPrompt(entity.getContent());
        var hfResponse = huggingFaceService.sendRequest(prompt);
        entity.setReviewSentiment(huggingFaceService.responseToText(hfResponse));

        reviewRepository.save(entity);
    }

    private String buildSentimentPrompt(String content) {
        return """
                You are tasked to evaluate text (from user reviews for movies) and determine the emotion of the user.
                In other words, you are performing sentiment analysis.
                After evaluation, you response should be very very simple..
                Always, respond ONLY with either "NEGATIVE" or "POSITIVE" and nothing else,
                and under no circumstances should you respond otherwise or spelling, etc
                With that out of the way, I want you to evaluate the following text:
                %s
                """.formatted(content);
    }
}