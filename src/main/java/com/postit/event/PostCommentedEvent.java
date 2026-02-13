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
public class PostCommentedEvent {
    private String commentId;
    private String postId;
    private String postOwnerId;
    private String commentUserId;
    private String commenterUsername;
    private String commentContent;
    private LocalDateTime timestamp;
}

