package com.postit.controller;

import com.postit.dto.NotificationResponse;
import com.postit.service.NotificationService;
import com.postit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        var user = userService.getUserByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId(), pageable);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResponse>> getUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        var user = userService.getUserByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getUnreadNotifications(user.getId(), pageable);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {

        var user = userService.getUserByUsername(userDetails.getUsername());
        long count = notificationService.getUnreadCount(user.getId());

        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {

        var user = userService.getUserByUsername(userDetails.getUsername());
        notificationService.markAllAsRead(user.getId());

        return ResponseEntity.noContent().build();
    }
}

