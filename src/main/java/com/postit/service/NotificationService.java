package com.postit.service;

import com.postit.dto.NotificationResponse;
import com.postit.event.PostCommentedEvent;
import com.postit.event.PostLikedEvent;
import com.postit.model.Notification;
import com.postit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createLikeNotification(PostLikedEvent event) {
        // Don't notify user about their own likes
        if (event.getLikeUserId().equals(event.getPostOwnerId())) {
            return;
        }

        Notification notification = Notification.builder()
            .userId(event.getPostOwnerId())
            .senderId(event.getLikeUserId())
            .senderUsername(event.getLikerUsername())
            .type(Notification.NotificationType.LIKE)
            .postId(event.getPostId())
            .message(event.getLikerUsername() + " liked your post")
            .build();

        notificationRepository.save(notification);
        log.info("Like notification created for user: {}", event.getPostOwnerId());
    }

    @Transactional
    public void createCommentNotification(PostCommentedEvent event) {
        // Don't notify user about their own comments
        if (event.getCommentUserId().equals(event.getPostOwnerId())) {
            return;
        }

        Notification notification = Notification.builder()
            .userId(event.getPostOwnerId())
            .senderId(event.getCommentUserId())
            .senderUsername(event.getCommenterUsername())
            .type(Notification.NotificationType.COMMENT)
            .postId(event.getPostId())
            .commentId(event.getCommentId())
            .message(event.getCommenterUsername() + " commented on your post")
            .build();

        notificationRepository.save(notification);
        log.info("Comment notification created for user: {}", event.getPostOwnerId());
    }

    public Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository
            .findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(this::mapToResponse);
    }

    public Page<NotificationResponse> getUnreadNotifications(String userId, Pageable pageable) {
        return notificationRepository
            .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable)
            .map(this::mapToResponse);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead(String userId) {
        // Get all unread notifications for the user (without pagination)
        notificationRepository.findAll().stream()
            .filter(n -> n.getUserId().equals(userId) && !n.isRead())
            .forEach(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .senderId(notification.getSenderId())
            .senderUsername(notification.getSenderUsername())
            .type(notification.getType().toString())
            .postId(notification.getPostId())
            .message(notification.getMessage())
            .read(notification.isRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}

