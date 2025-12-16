package com.clashofserres.cinematch.repository;

import com.clashofserres.cinematch.data.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    // Find a user by his username
    Optional<UserEntity> findByUsername(String username);
    // Find a user by his email
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByIdIsIn(Collection<Long> ids);

    boolean existsByEmail(String email);

}