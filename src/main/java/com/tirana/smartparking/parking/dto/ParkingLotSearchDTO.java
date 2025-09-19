package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSpace;

import java.time.Instant;
import java.util.List;

public record ParkingLotSearchDTO(
        Long id,
        String name,
        String description,
        String address,
        String operatingHours,
        ParkingLot.Status status,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Boolean publicAccess,
        Boolean hasChargingStations,
        Boolean hasDisabledAccess,
        Boolean hasCctv,
        Boolean covered,
        Integer capacity,
        Integer availableSpaces,
        Double availabilityPercentage,
        Instant availabilityUpdatedAt,
        Double averageRating,
        Integer totalReviews,
        List<ParkingSpace.SpaceType> availableSpaceTypes
) {
}
