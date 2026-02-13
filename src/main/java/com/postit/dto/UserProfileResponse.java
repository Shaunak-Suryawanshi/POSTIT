package com.postit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String profileImageUrl;
    private long followersCount;
    private long followingCount;
    private long postsCount;
    private boolean followedByCurrentUser;
    private LocalDateTime createdAt;
}
