package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record PricingQuoteDTO(
        Long parkingLotId,
        Long parkingSpaceId,
        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType,
        @NotNull(message = "User group is required")
        UserGroup userGroup,
        @NotNull(message = "Start time is required")
        ZonedDateTime startTime,
        @NotNull(message = "End time is required")
        ZonedDateTime endTime
) {
    @AssertTrue(message = "Either parkingLotId or parkingSpaceId must be provided")
    public boolean isLotOrSpaceProvided() {
        return parkingLotId != null || parkingSpaceId != null;
    }
}
