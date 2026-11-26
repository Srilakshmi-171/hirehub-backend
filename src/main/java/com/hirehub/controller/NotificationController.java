package com.hirehub.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hirehub.dto.NotificationResponse;
import com.hirehub.service.NotificationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Get all my notifications
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<NotificationResponse> getMyNotifications(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return notificationService.getMyNotifications(userId);
    }

    // Get unread notification count
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/unread-count")
    public long getUnreadCount(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return notificationService.getUnreadCount(userId);
    }

    // Mark one notification as read
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return notificationService.markAsRead(
                notificationId, userId);
    }

    // Mark all notifications as read
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/read-all")
    public int markAllAsRead(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return notificationService.markAllAsRead(userId);
    }
}