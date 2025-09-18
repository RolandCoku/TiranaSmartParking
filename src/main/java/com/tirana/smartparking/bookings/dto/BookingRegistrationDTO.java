package com.tirana.smartparking.bookings.dto;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.ZonedDateTime;

public record BookingRegistrationDTO(
        @NotNull(message = "Parking space ID is required")
        Long parkingSpaceId,
        
        @NotNull(message = "Vehicle plate is required")
        String vehiclePlate,
        
        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType,
        
        @NotNull(message = "User group is required")
        UserGroup userGroup,
        
        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        ZonedDateTime startTime,
        
        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        ZonedDateTime endTime,
        
        String paymentMethodId,
        String notes
) {
}