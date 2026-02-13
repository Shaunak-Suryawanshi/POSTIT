package com.postit.controller;

import com.postit.dto.UserProfileResponse;
import com.postit.model.User;
import com.postit.service.FollowService;
import com.postit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        followService.followUser(currentUser.getId(), userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        followService.unfollowUser(currentUser.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserProfileResponse>> getFollowers(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        List<UserProfileResponse> followers = followService.getFollowers(userId, currentUser.getId());
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserProfileResponse>> getFollowing(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        List<UserProfileResponse> following = followService.getFollowing(userId, currentUser.getId());
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Boolean>> getFollowStatus(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        boolean isFollowing = followService.isFollowing(currentUser.getId(), userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    }
}
