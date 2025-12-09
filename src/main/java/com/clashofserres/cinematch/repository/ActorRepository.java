package com.clashofserres.cinematch.repository;


import com.clashofserres.cinematch.data.model.ActorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<ActorEntity, Long> {
    Optional<ActorEntity> findById(Long id);
    Optional<ActorEntity> findByName(String name);
}