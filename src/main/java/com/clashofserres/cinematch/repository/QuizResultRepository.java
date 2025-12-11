package com.clashofserres.cinematch.data.repository;

import com.clashofserres.cinematch.data.model.QuizResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResultEntity, Long> {
    List<QuizResultEntity> findByUserId(Long userId);
}