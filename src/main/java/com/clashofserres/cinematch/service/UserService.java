package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserService {

    // Thrown when trying to register with bad credentials
    // e.g. username already taken, password too weak, etc.
    public class InvalidCredentials extends Exception
    {
        public InvalidCredentials(String message)
        {
            super(message);
        }
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity getMyUser()
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public Optional<UserEntity> getMyUserOptional()
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    public boolean isLoggedIn()
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).isPresent();
    }

    public UserEntity registerUser(String username, String email, String password) throws InvalidCredentials {

        if (username.isEmpty())
        {
            throw new InvalidCredentials("Username cannot be empty");
        }

        if (password.isEmpty())
        {
            throw new InvalidCredentials("Password cannot be empty");
        }

        if (email.isEmpty())
        {
            throw new InvalidCredentials("Email cannot be empty");
        }

        if (userRepository.findByUsername(username).isPresent())
        {
            throw new InvalidCredentials("Username already taken");
        }

        //if (password.length() < 8)
        //{
        //	throw new InvalidCredentials("Password too weak");
        //}

        if (userRepository.findByEmail(email).isPresent())
        {
            throw new InvalidCredentials("Email already taken");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);
        return user;
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }


}