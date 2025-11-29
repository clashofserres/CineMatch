package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserEntity registerUser(String username, String email, String password) {


        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username '" + username + "' already taken.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use.");
        }


        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setEmail(email);


        newUser.setPasswordHash(password);


        return userRepository.save(newUser);
    }


    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}