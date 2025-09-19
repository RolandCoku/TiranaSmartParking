package com.tirana.smartparking.notification.dto;

import com.tirana.smartparking.notification.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record NotificationRequestDTO(
        @NotEmpty(message = "Recipients list cannot be empty")
        List<Long> recipients,
        
        @NotNull(message = "Notification type is required")
        Notification.NotificationType notificationType,
        
        @NotEmpty(message = "Channels list cannot be empty")
        List<Notification.NotificationChannel> channels,
        
        @NotBlank(message = "Title is required")
        String title,
        
        @NotBlank(message = "Message is required")
        String message,
        
        String templateId,
        
        Map<String, Object> data,
        
        Notification.NotificationPriority priority,
        
        Instant scheduledAt
) {
    public NotificationRequestDTO {
        if (priority == null) {
            priority = Notification.NotificationPriority.MEDIUM;
        }
    }
}
