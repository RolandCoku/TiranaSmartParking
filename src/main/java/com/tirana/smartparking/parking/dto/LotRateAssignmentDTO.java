package com.tirana.smartparking.parking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.time.ZonedDateTime;

public record LotRateAssignmentDTO(
        Long id,
        @NotNull(message = "Parking lot ID is required")
        Long parkingLotId,
        @NotNull(message = "Rate plan ID is required")
        Long ratePlanId,
        @PositiveOrZero(message = "Priority must be positive or zero")
        Integer priority,
        ZonedDateTime effectiveFrom,
        ZonedDateTime effectiveTo,
        Instant createdAt,
        Instant updatedAt
) {
}
