package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingSession.SessionStatus;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;

import java.time.Instant;
import java.time.ZonedDateTime;

public record ParkingSessionDTO(
        Long id,
        Long userId,
        String userEmail,
        Long parkingSpaceId,
        String parkingSpaceLabel,
        Long parkingLotId,
        String parkingLotName,
        String parkingLotAddress,
        String vehiclePlate,
        VehicleType vehicleType,
        UserGroup userGroup,
        ZonedDateTime startedAt,
        ZonedDateTime endedAt,
        Integer billedAmount,
        String currency,
        SessionStatus status,
        String sessionReference,
        String paymentMethodId,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
