package com.postit.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikedEvent {
    private String likeId;
    private String postId;
    private String postOwnerId;
    private String likeUserId;
    private String likerUsername;
    private LocalDateTime timestamp;
}

