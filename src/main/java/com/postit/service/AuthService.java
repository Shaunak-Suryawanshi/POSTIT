package com.postit.service;

import com.postit.dto.*;
import com.postit.exception.InvalidCredentialsException;
import com.postit.exception.ResourceNotFoundException;
import com.postit.exception.UserAlreadyExistsException;
import com.postit.model.RefreshToken;
import com.postit.model.User;
import com.postit.repository.RefreshTokenRepository;
import com.postit.repository.UserRepository;
import com.postit.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                .provider("local")
                .roles(roles)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        // Generate tokens
        String accessToken = tokenProvider.generateToken(user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

        // Save refresh token
        saveRefreshToken(user.getId(), refreshToken);

        // Get user profile
        UserProfileResponse userProfile = userService.getUserProfile(user.getId(), null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userProfile)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()));

            // Get user
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Generate tokens
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

            // Save refresh token
            refreshTokenRepository.deleteByUserId(user.getId());
            saveRefreshToken(user.getId(), refreshToken);

            // Get user profile
            UserProfileResponse userProfile = userService.getUserProfile(user.getId(), null);

            log.info("User logged in: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userProfile)
                    .build();

        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        // Validate refresh token
        if (!tokenProvider.validateToken(refreshTokenValue)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        // Check if expired
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidCredentialsException("Refresh token expired");
        }

        // Get user
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate new access token
        String newAccessToken = tokenProvider.generateToken(user.getUsername());

        // Get user profile
        UserProfileResponse userProfile = userService.getUserProfile(user.getId(), null);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenValue)
                .user(userProfile)
                .build();
    }

    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("User logged out: {}", userId);
    }

    private void saveRefreshToken(String userId, String token) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiryDate(expiryDate)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
