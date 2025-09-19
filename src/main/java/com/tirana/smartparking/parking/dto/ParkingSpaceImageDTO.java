package com.tirana.smartparking.parking.dto;

import java.time.Instant;

public record ParkingSpaceImageDTO(
        Long id,
        Long parkingSpaceId,
        String imageUrl,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
