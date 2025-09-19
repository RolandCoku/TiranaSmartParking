package com.tirana.smartparking.notification.dto;

import com.tirana.smartparking.notification.entity.Notification;
import com.tirana.smartparking.notification.entity.NotificationRecipient;

import java.time.Instant;

public record DeliveryDetailDTO(
        Long userId,
        String userEmail,
        Notification.NotificationChannel channel,
        NotificationRecipient.DeliveryStatus status,
        Instant deliveredAt,
        String errorMessage
) {}
