package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingSpace;

import java.time.Instant;

public record ParkingSpaceSummaryDTO(
        Long id,
        Long parkingLotId,
        String parkingLotName,
        Double latitude,
        Double longitude,
        ParkingSpace.SpaceType spaceType,
        ParkingSpace.SpaceStatus spaceStatus,
        String label,
        String description,
        Boolean hasSensor,
        Long sensorDeviceId,
        Instant lastStatusChangedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
