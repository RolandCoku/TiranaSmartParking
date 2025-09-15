package com.tirana.smartparking.parking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record ParkingSpaceRegistrationDTO(
    Long parkingLotId,
    @DecimalMin("-180.0") @DecimalMax("180.0")
    Double longitude,
    @DecimalMin("-90.0") @DecimalMax("90.0")
    Double latitude,
    String spaceType,
    String spaceStatus,
    String label,
    String description,
    Long sensorId
) {
    @AssertTrue(message = "Either parkingLotId or BOTH coordinates must be provided")
    public boolean isParentOrLocationProvided() {
        return parkingLotId != null || (latitude != null && longitude != null);
    }
}