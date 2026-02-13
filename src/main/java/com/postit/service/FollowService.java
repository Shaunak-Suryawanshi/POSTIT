package com.postit.service;

import com.postit.dto.UserProfileResponse;
import com.postit.exception.ResourceNotFoundException;
import com.postit.model.Follow;
import com.postit.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    @Transactional
    public void followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            Follow follow = Follow.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .build();

            followRepository.save(follow);
            log.info("User {} followed user {}", followerId, followingId);
        }
    }

    @Transactional
    public void unfollowUser(String followerId, String followingId) {
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            log.info("User {} unfollowed user {}", followerId, followingId);
        }
    }

    public List<UserProfileResponse> getFollowers(String userId, String currentUserId) {
        List<Follow> follows = followRepository.findByFollowingId(userId);

        return follows.stream()
                .map(follow -> userService.getUserProfile(follow.getFollowerId(), currentUserId))
                .collect(Collectors.toList());
    }

    public List<UserProfileResponse> getFollowing(String userId, String currentUserId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);

        return follows.stream()
                .map(follow -> userService.getUserProfile(follow.getFollowingId(), currentUserId))
                .collect(Collectors.toList());
    }

    public boolean isFollowing(String followerId, String followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
