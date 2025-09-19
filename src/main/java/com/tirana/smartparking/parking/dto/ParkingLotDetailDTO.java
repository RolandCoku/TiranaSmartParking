package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingLot;

import java.time.Instant;
import java.util.List;

public record ParkingLotDetailDTO(
        Long id,
        String name,
        String description,
        String address,
        String phone,
        String email,
        String operatingHours,
        ParkingLot.Status status,
        Double latitude,
        Double longitude,
        Boolean publicAccess,
        Boolean hasChargingStations,
        Boolean hasDisabledAccess,
        Boolean hasCctv,
        Boolean covered,
        Integer capacity,
        Integer availableSpaces,
        Integer occupiedSpaces,
        Double availabilityPercentage,
        Instant availabilityUpdatedAt,
        List<ParkingSpaceSummaryDTO> parkingSpaces,
        List<ReviewSummaryDTO> reviews,
        Double averageRating,
        Integer totalReviews,
        Instant createdAt,
        Instant updatedAt
) {
}
