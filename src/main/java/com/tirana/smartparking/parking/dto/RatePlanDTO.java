package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.Enum.RateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

public record RatePlanDTO(
        Long id,
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Rate type is required")
        RateType type,
        @NotBlank(message = "Currency is required")
        String currency,
        @NotBlank(message = "Time zone is required")
        String timeZone,
        @PositiveOrZero(message = "Grace minutes must be positive or zero")
        Integer graceMinutes,
        @PositiveOrZero(message = "Increment minutes must be positive or zero")
        Integer incrementMinutes,
        @PositiveOrZero(message = "Daily cap must be positive or zero")
        Integer dailyCap,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
