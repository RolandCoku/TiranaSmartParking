package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingSpace;

import java.util.List;

public record AvailabilityInfoDTO(
        Long parkingLotId,
        Integer availableSpaces,
        Double availabilityPercentage,
        List<ParkingSpace.SpaceType> availableSpaceTypes
) {
}
