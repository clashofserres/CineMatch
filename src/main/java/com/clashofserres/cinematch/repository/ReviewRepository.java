package com.clashofserres.cinematch.repository;

import com.clashofserres.cinematch.data.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findById(long id);
    List<ReviewEntity> findAllByUserId(long id);
    List<ReviewEntity> findALlByMovieId(long id);
}
