package com.postit.service;

import com.postit.dto.UserProfileResponse;
import com.postit.exception.ResourceNotFoundException;
import com.postit.model.User;
import com.postit.repository.FollowRepository;
import com.postit.repository.PostRepository;
import com.postit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    public UserProfileResponse getUserProfile(String userId, String currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long followersCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        long postsCount = postRepository.countByUserIdAndIsDeletedFalse(userId);

        boolean followedByCurrentUser = false;
        if (currentUserId != null && !currentUserId.equals(userId)) {
            followedByCurrentUser = followRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .postsCount(postsCount)
                .followedByCurrentUser(followedByCurrentUser)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserProfileResponse getUserProfileByUsername(String username, String currentUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return getUserProfile(user.getId(), currentUserId);
    }

    public User getCurrentUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
}
