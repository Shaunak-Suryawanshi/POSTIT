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
public class PostResponse {
    private String id;
    private String userId;
    private String username;
    private String displayName;
    private String content;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
