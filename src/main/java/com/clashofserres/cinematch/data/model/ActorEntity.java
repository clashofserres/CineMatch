package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.jspecify.annotations.Nullable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actor")
public class ActorEntity extends AbstractEntity<Long> {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    // Η άλλη πλευρά της σχέσης (mappedBy)
    @ManyToMany(mappedBy = "cast")
    private Set<MovieEntity> movies = new HashSet<>();

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters & Setters

    public String getName() { return name; }
    public void setName(String firstName) { this.name = firstName; }

    public Set<MovieEntity> getMovies() { return movies; }
    public void setMovies(Set<MovieEntity> movies) { this.movies = movies; }
}