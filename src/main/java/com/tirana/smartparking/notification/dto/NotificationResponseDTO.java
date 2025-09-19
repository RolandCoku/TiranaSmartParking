package com.tirana.smartparking.notification.dto;

import com.tirana.smartparking.notification.entity.Notification;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record NotificationResponseDTO(
        Long id,
        String notificationId,
        Notification.NotificationType type,
        String title,
        String message,
        Map<String, Object> data,
        List<Notification.NotificationChannel> channels,
        Notification.NotificationPriority priority,
        Notification.NotificationStatus status,
        String templateId,
        Instant scheduledAt,
        Instant createdAt,
        Instant updatedAt,
        List<com.tirana.smartparking.notification.dto.NotificationRecipientDTO> recipients
) {}
