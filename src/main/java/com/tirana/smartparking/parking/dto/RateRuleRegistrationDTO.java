package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record RateRuleRegistrationDTO(
        @NotNull(message = "Rate plan ID is required")
        Long ratePlanId,
        @PositiveOrZero(message = "Start minute must be positive or zero")
        Integer startMinute,
        @PositiveOrZero(message = "End minute must be positive or zero")
        Integer endMinute,
        LocalTime startTime,
        LocalTime endTime,
        DayOfWeek dayOfWeek,
        VehicleType vehicleType,
        UserGroup userGroup,
        @PositiveOrZero(message = "Price per hour must be positive or zero")
        Integer pricePerHour,
        @PositiveOrZero(message = "Flat price must be positive or zero")
        Integer priceFlat
) {
}
