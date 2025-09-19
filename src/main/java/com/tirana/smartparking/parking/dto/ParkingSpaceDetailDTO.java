package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingSpace;

import java.time.Instant;
import java.util.List;

public record ParkingSpaceDetailDTO(
        Long id,
        Long parkingLotId,
        String parkingLotName,
        String parkingLotAddress,
        Double latitude,
        Double longitude,
        ParkingSpace.SpaceType spaceType,
        ParkingSpace.SpaceStatus spaceStatus,
        String label,
        String description,
        Boolean hasSensor,
        Long sensorDeviceId,
        String sensorStatus,
        Instant lastStatusChangedAt,
        List<String> images,
        List<ReviewSummaryDTO> reviews,
        Double averageRating,
        Integer totalReviews,
        Instant createdAt,
        Instant updatedAt
) {
}
