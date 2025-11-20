package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actor")
public class ActorEntity extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate birthDate;

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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Set<MovieEntity> getMovies() { return movies; }
    public void setMovies(Set<MovieEntity> movies) { this.movies = movies; }

    // Helper method για εμφάνιση ονόματος
    public String getFullName() {
        return firstName + " " + lastName;
    }
}