package com.clashofserres.cinematch.repository;

import com.clashofserres.cinematch.data.model.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    Optional<MovieEntity> findById(Long id);
    Optional<MovieEntity> findByTitle(String title);
}
