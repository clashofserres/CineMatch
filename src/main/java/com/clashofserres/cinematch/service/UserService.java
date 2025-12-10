package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserService {

    // Thrown when trying to register or update with bad credentials
    public static class InvalidCredentials extends Exception {
        public InvalidCredentials(String message) {
            super(message);
        }
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity getMyUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public Optional<UserEntity> getMyUserOptional() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    public boolean isLoggedIn() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).isPresent();
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public UserEntity registerUser(String username, String email, String password) throws InvalidCredentials {

        if (username.isEmpty()) {
            throw new InvalidCredentials("Username cannot be empty");
        }

        if (password.isEmpty()) {
            throw new InvalidCredentials("Password cannot be empty");
        }

        if (email.isEmpty()) {
            throw new InvalidCredentials("Email cannot be empty");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new InvalidCredentials("Username already taken");
        }

        if (userRepository.findByEmail(email).isPresent()) {
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

    /**
     * Updates non-security related profile information (Username, Email).
     */
    public UserEntity updateProfile(String newUsername, String newEmail) throws InvalidCredentials {

        UserEntity user = getMyUser();

        if (newUsername.isEmpty()) {
            throw new InvalidCredentials("Username cannot be empty");
        }

        if (newEmail.isEmpty()) {
            throw new InvalidCredentials("Email cannot be empty");
        }

        // Username changed? Check if taken by another user.
        if (!newUsername.equals(user.getUsername()) &&
                userRepository.findByUsername(newUsername).isPresent()) {
            throw new InvalidCredentials("Username already taken");
        }

        // Email changed? Check if taken by another user.
        if (!newEmail.equals(user.getEmail()) &&
                userRepository.findByEmail(newEmail).isPresent()) {
            throw new InvalidCredentials("Email already taken");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);

        userRepository.save(user);

        // Refresh the security context so the logged-in session reflects the new username
        updateSecurityContext(user);

        return user;
    }

    /**
     * Updates the password. Requires the current password for verification.
     */
    public void changePassword(String currentPassword, String newPassword) throws InvalidCredentials {
        UserEntity user = getMyUser();

        // 1. Verify the old password is correct
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentials("Current password is incorrect");
        }

        // 2. Validate new password inputs
        if (newPassword == null || newPassword.isEmpty()) {
            throw new InvalidCredentials("New password cannot be empty");
        }

        // (Optional) Add strength checks here, e.g., length > 6
        if (newPassword.length() < 6) {
            throw new InvalidCredentials("Password is too short (min 6 characters)");
        }

        // 3. Update the password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 4. Update Context to ensure consistency
        updateSecurityContext(user);
    }

    private void updateSecurityContext(UserEntity user) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPasswordHash(),
                currentAuth.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}