package com.postit.repository;

import com.postit.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Get all notifications for a user
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Get unread notifications for a user
    Page<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Count unread notifications
    long countByUserIdAndReadFalse(String userId);
}

