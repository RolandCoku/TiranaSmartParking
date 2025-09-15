package com.tirana.smartparking.parking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;

public record SpaceRateOverrideRegistrationDTO(
        @NotNull(message = "Parking space ID is required")
        Long parkingSpaceId,
        @NotNull(message = "Rate plan ID is required")
        Long ratePlanId,
        @PositiveOrZero(message = "Priority must be positive or zero")
        Integer priority,
        ZonedDateTime effectiveFrom,
        ZonedDateTime effectiveTo
) {
}
