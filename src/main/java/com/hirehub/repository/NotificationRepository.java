package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get all notifications for a user, newest first
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(
            Long recipientId);

    // Get unread notifications for a user
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
            Long recipientId);

    // Count unread notifications
    long countByRecipientIdAndIsReadFalse(Long recipientId);
}