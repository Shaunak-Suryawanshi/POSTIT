package com.postit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Indexed
    private String userId;                 // Who receives the notification

    private String senderId;               // Who triggered it (liker/commenter)
    private String senderUsername;

    private NotificationType type;         // LIKE, COMMENT

    private String postId;
    private String commentId;

    private String message;

    @Builder.Default
    private boolean read = false;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum NotificationType {
        LIKE,
        COMMENT,
        FOLLOW,
        REPLY
    }
}

