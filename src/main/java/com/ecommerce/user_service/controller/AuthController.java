package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.ApiResponse;
import com.ecommerce.user_service.dto.SignupRequest;
import com.ecommerce.user_service.service.UserService;
import com.ecommerce.user_service.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    public UserService userService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupRequest user) {
        try {
            log.info("Signing up user: {}", user.getEmail());
            String token = userService.signup(user);
            return ResponseEntity.ok(new ApiResponse<>("User signed up successfully", token));
        } catch (Exception e) {
            log.error("Error during signup: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>("Signup failed", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Map<String, String> request) {
        try {
            String token = userService.login(request.get("email"), request.get("password"));
            return ResponseEntity.ok(new ApiResponse<>("Login successful", token));
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>("Login failed", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            log.info("Deleting user with token: {}", token);
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            userService.deleteByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", null));
        } catch (Exception e) {
            log.error("Error during user deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>("User deletion failed", e.getMessage()));
        }
    }
}