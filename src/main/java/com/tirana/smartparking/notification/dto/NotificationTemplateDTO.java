package com.tirana.smartparking.notification.dto;

import com.tirana.smartparking.notification.entity.Notification;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record NotificationTemplateDTO(
        Long id,
        String templateId,
        String name,
        Notification.NotificationType type,
        List<Notification.NotificationChannel> channels,
        String subjectTemplate,
        String bodyTemplate,
        Map<String, Object> variables,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
