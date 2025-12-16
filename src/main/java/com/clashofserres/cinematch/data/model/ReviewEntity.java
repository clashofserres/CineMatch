package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class ReviewEntity extends AbstractEntity<Long> {

    public static final int REVIEW_MAX_LENGHT = 2048;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private long movieId;

    @Column(nullable = false)
    private String movieTitle;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = REVIEW_MAX_LENGHT)
    private String content;

    @Column(nullable = true)
    private String attachedFileGUID;

    @Column(nullable = true)
    private String reviewSentiment;

    public ReviewEntity() {

    }
    public ReviewEntity(Long userId) {
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachedFileGUID() {
        return attachedFileGUID;
    }

    public void setAttachedFileGUID(String attachedFileGuid) {
        this.attachedFileGUID = attachedFileGuid;
    }

    public String getReviewSentiment() {
        return reviewSentiment;
    }
    public void setReviewSentiment(String reviewSentiment) {
        this.reviewSentiment = reviewSentiment;
    }
}