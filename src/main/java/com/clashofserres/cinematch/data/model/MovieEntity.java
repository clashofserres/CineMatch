package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
public class MovieEntity extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000) // Μεγαλύτερο κείμενο για την περιγραφή
    private String plot;

    private LocalDate releaseDate;

    private String posterUrl;

    private String genre;

    // Σχέση Many-to-Many με Actors
    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<ActorEntity> cast = new HashSet<>();

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters & Setters

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPlot() { return plot; }
    public void setPlot(String plot) { this.plot = plot; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Set<ActorEntity> getCast() { return cast; }
    public void setCast(Set<ActorEntity> cast) { this.cast = cast; }
}