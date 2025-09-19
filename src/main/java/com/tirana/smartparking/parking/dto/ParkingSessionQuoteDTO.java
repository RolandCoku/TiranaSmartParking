package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record ParkingSessionQuoteDTO(
        @NotNull(message = "Parking space ID is required")
        Long parkingSpaceId,
        
        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType,
        
        @NotNull(message = "User group is required")
        UserGroup userGroup,
        
        @NotNull(message = "Start time is required")
        @FutureOrPresent(message = "Start time must be in the present or future")
        ZonedDateTime startTime,
        
        @NotNull(message = "End time is required")
        @FutureOrPresent(message = "End time must be in the present or future")
        ZonedDateTime endTime
) {}
