package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResultEntity extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Optional: Αν θέλουμε να ξέρουμε ποιο είδος αφορούσε το κουίζ
    private Long relatedGenreId;

    public QuizResultEntity() {}

    public QuizResultEntity(Long userId, int score) {
        this.userId = userId;
        this.score = score;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Getters & Setters...
    public Long getUserId() { return userId; }
    public int getScore() { return score; }
    public LocalDateTime getTimestamp() { return timestamp; }
}