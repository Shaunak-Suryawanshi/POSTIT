package com.postit.controller;

import com.postit.dto.UserProfileResponse;
import com.postit.exception.ResourceNotFoundException;
import com.postit.model.User;
import com.postit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("getCurrentUser called with null authentication principal");
            throw new ResourceNotFoundException("User not authenticated");
        }

        try {
            User user = userService.getUserByUsername(userDetails.getUsername());
            UserProfileResponse profile = userService.getUserProfile(user.getId(), user.getId());
            log.debug("Retrieved current user profile: {}", user.getUsername());
            return ResponseEntity.ok(profile);
        } catch (ResourceNotFoundException e) {
            log.error("User not found: {}", userDetails.getUsername());
            throw e;
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("getUserByUsername called with null authentication principal");
            throw new ResourceNotFoundException("User not authenticated");
        }

        try {
            User currentUser = userService.getUserByUsername(userDetails.getUsername());
            UserProfileResponse profile = userService.getUserProfileByUsername(username, currentUser.getId());
            return ResponseEntity.ok(profile);
        } catch (ResourceNotFoundException e) {
            log.error("User not found: {}", username);
            throw e;
        }
    }
}
