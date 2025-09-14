package com.tirana.smartparking.parking.dto;

import com.tirana.smartparking.parking.entity.ParkingLot;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ParkingLotRegistrationDTO(
        @NotNull ParkingLot.Source source,
        String osmType,
        Long osmId,
        String name,
        String description,
        String address,

        boolean active,
        boolean publicAccess,
        boolean hasChargingStations,
        boolean hasDisabledAccess,
        boolean hasCctv,

        int capacity,
        int availableSpaces,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        Double longitude,
        @DecimalMin("-90.0") @DecimalMax("90.0")
        Double latitude
) {
}
