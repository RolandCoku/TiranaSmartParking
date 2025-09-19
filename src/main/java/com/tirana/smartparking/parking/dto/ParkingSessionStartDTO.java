package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParkingSessionStartDTO(
        @NotNull(message = "Parking space ID is required")
        Long parkingSpaceId,
        
        @NotBlank(message = "Vehicle plate is required")
        String vehiclePlate,
        
        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType,
        
        @NotNull(message = "User group is required")
        UserGroup userGroup,
        
        String paymentMethodId,
        
        String notes
) {}
