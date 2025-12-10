package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.jspecify.annotations.Nullable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
public class MovieEntity extends AbstractEntity<Long> {

    @Id
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "vote_average")
    private Double voteAverage;

    @Column(length = 2000)
    private String overview;

    private LocalDate releaseDate;

    private String posterPath;



    private Set<Long> genreIds = new HashSet<>();

    @ManyToMany(mappedBy = "watchList")
    private Set<UserEntity> watchList = new HashSet<>();



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



    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String plot) { this.overview = plot; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterUrl) { this.posterPath = posterUrl; }

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Set<ActorEntity> getCast() { return cast; }
    public void setCast(Set<ActorEntity> cast) { this.cast = cast; }

    public Set<UserEntity> getWatchList() {
        return watchList;
    }

    public void setWatchList(Set<UserEntity> watchList) {
        this.watchList = watchList;
    }
}