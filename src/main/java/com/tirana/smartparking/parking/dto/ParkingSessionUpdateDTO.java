package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.parking.entity.ParkingSession.SessionStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.ZonedDateTime;

public record ParkingSessionUpdateDTO(
        String vehiclePlate,
        VehicleType vehicleType,
        UserGroup userGroup,
        @FutureOrPresent(message = "End time must be in the present or future")
        ZonedDateTime endTime,
        SessionStatus status,
        String paymentMethodId,
        String notes
) {}
