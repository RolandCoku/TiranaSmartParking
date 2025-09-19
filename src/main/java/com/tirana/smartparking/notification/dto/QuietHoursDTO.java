package com.tirana.smartparking.notification.dto;

public record QuietHoursDTO(
        Boolean enabled,
        String startTime,
        String endTime,
        String timezone
) {}
