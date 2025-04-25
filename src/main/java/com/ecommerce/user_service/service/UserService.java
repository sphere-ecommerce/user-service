package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.SignupRequest;
import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.utils.JwtUtil;
import jakarta.persistence.GeneratedValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public String signup(SignupRequest requestBody) {
        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByEmail(requestBody.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Create and save the new user
        User user = new User();
        user.setEmail(requestBody.getEmail());
        user.setPassword(hashPassword(requestBody.getPassword()));
        user.setUsername(requestBody.getUsername());
        userRepository.save(user);

        // Generate and return a JWT token
        return jwtUtil.generateToken(user.getEmail());
    }

    public String login(String email, String password) {
        // Find the user by email
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Validate the password
        if (user.getPassword().equals(hashPassword(password))) {
            return jwtUtil.generateToken(email);
        }

        throw new RuntimeException("Invalid credentials");
    }

    public void deleteByEmail(String email) {
        // Find the user by email
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Delete the user
        userRepository.delete(user);
    }
}