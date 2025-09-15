package com.tirana.smartparking.parking.dto;

import java.time.Instant;

public record ParkingLotResponseDTO(
        Long id,
        String name,
        String location,
        String description,
        int totalSpaces,
        int availableSpaces,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
