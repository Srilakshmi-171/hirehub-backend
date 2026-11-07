package com.hirehub.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hirehub.dto.NotificationResponse;
import com.hirehub.entity.Notification;
import com.hirehub.entity.User;
import com.hirehub.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Create a notification
    public void createNotification(
            User recipient,
            String message,
            String type) {

        Notification notification = new Notification();

        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    // Get all notifications for a user
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(
            Long recipientId) {

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientIdOrderByCreatedAtDesc(
                                recipientId);

        return notifications.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get unread notification count
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId) {

        return notificationRepository
                .countByRecipientIdAndIsReadFalse(recipientId);
    }

    // Mark one notification as read
    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId,
            Long recipientId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"));

        if (!notification.getRecipient().getId()
                .equals(recipientId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access this notification");
        }

        notification.setRead(true);

        Notification updated =
                notificationRepository.save(notification);

        return convertToResponse(updated);
    }

    // Mark all notifications as read
    @Transactional
    public int markAllAsRead(Long recipientId) {

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
                                recipientId);

        notifications.forEach(notification ->
                notification.setRead(true));

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    // Convert entity into response DTO
    private NotificationResponse convertToResponse(
            Notification notification) {

        NotificationResponse response =
                new NotificationResponse();

        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }
}