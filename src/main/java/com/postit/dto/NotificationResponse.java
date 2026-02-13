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
public class NotificationResponse {
    private String id;
    private String senderId;
    private String senderUsername;
    private String type;                // LIKE, COMMENT
    private String postId;
    private String commentId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}

