package com.tirana.smartparking.maintenance.dto;

import java.time.ZonedDateTime;

public record SchedulerJobInfoDTO(
        String jobName,
        String description,
        String cronExpression,
        Long delayMs,
        ZonedDateTime lastExecutionTime,
        ZonedDateTime nextExecutionTime,
        String status,
        String lastExecutionStatus,
        Integer processedCount,
        String errorMessage
) {
}
