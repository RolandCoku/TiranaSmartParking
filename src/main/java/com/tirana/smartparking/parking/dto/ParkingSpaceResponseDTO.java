package com.tirana.smartparking.parking.dto;

import java.time.Instant;

public record ParkingSpaceResponseDTO(
        Long id,
        Long parkingLotId,
        Double locationX,
        Double locationY,
        String type,
        String status,
        String label,
        String description,
        Long sensorDeviceId,
        Instant createdAt,
        Instant updatedAt
) {
}
