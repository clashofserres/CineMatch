package com.clashofserres.cinematch.data.model;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "application_user")
public class UserEntity extends AbstractEntity<Long> {

    public static final int USERNAME_MAX_LENGTH = 32;
    public static final int PASSWORD_MAX_LENGTH = 64;
    public static final int EMAIL_MAX_LENGTH = 128;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;

    @Column(nullable = false, length = PASSWORD_MAX_LENGTH)
    private String passwordHash;

    @Column(nullable = false, length = EMAIL_MAX_LENGTH)
    private String email;

    // Προαιρετικά: Role για δικαιώματα (ADMIN, USER)
    private String role;

    // TODO Moreo optimized way without using EAGER and possibly use DTOS
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_movie_watchlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<MovieEntity> watchList = new HashSet<>();

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<MovieEntity> getWatchList() {
        return watchList;
    }

    public void setWatchList(Set<MovieEntity> watchList) {
        this.watchList = watchList;
    }

}