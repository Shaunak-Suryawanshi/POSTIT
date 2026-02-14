package com.postit.controller;

import com.postit.dto.*;
import com.postit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register attempt for username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", request.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        log.info("User logged in successfully: {}", request.getUsernameOrEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh requested");
        AuthResponse response = authService.refreshToken(request);
        log.debug("Token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.debug("Logout requested");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            log.info("Health check - Authenticated user: {}", userDetails.getUsername());
            return ResponseEntity.ok("OK - Authenticated as " + userDetails.getUsername());
        }
        log.info("Health check - No authentication");
        return ResponseEntity.ok("OK - Not authenticated");
    }
}
