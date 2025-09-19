package com.tirana.smartparking.notification.dto;

import java.time.Instant;
import java.util.Map;

public record NotificationPreferencesDTO(
        Long userId,
        Boolean emailEnabled,
        Boolean smsEnabled,
        Boolean pushEnabled,
        Boolean inAppEnabled,
        Map<String, TypePreferenceDTO> preferences,
        QuietHoursDTO quietHours,
        Instant createdAt,
        Instant updatedAt
) {}

