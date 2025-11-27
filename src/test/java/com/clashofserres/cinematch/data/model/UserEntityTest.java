package com.clashofserres.cinematch.data.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import jakarta.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateValidUser_shouldSucceed() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPasswordHash("password123");
        user.setEmail("test@example.com");
        user.setRole("USER");

        UserEntity savedUser = entityManager.persistAndFlush(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testCreateUserWithNullUsername_shouldFail() {
        UserEntity user = new UserEntity();
        user.setUsername(null);
        user.setPasswordHash("password123");
        user.setEmail("test@example.com");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user);
        });
    }

    @Test
    void testCreateUserWithNullPassword_shouldFail() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPasswordHash(null);
        user.setEmail("test@example.com");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user);
        });
    }

    @Test
    void testCreateUserWithDuplicateUsername_shouldFail() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("duplicateuser");
        user1.setPasswordHash("password123");
        user1.setEmail("test1@example.com");
        entityManager.persistAndFlush(user1);

        UserEntity user2 = new UserEntity();
        user2.setUsername("duplicateuser");
        user2.setPasswordHash("password456");
        user2.setEmail("test2@example.com");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user2);
        });
    }
}
