package com.postit.controller;

import com.postit.dto.UserProfileResponse;
import com.postit.model.User;
import com.postit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        UserProfileResponse profile = userService.getUserProfile(user.getId(), user.getId());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        UserProfileResponse profile = userService.getUserProfileByUsername(username, currentUser.getId());
        return ResponseEntity.ok(profile);
    }
}
