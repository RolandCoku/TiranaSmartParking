package com.tirana.smartparking.notification.dto;

import com.tirana.smartparking.notification.entity.Notification;
import com.tirana.smartparking.notification.entity.NotificationRecipient;

import java.time.Instant;

public record NotificationRecipientDTO(
        Long id,
        Long userId,
        String userEmail,
        Notification.NotificationChannel channel,
        NotificationRecipient.DeliveryStatus status,
        Instant deliveredAt,
        Instant readAt,
        String errorMessage,
        Integer retryCount,
        Instant createdAt,
        Instant updatedAt
) {}
