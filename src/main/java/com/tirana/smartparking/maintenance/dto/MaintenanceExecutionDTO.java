package com.tirana.smartparking.maintenance.dto;

import java.time.ZonedDateTime;

public record MaintenanceExecutionDTO(
        String operation,
        String description,
        ZonedDateTime executedAt,
        String status,
        Integer processedCount,
        String errorMessage,
        Long executionTimeMs
) {
}
