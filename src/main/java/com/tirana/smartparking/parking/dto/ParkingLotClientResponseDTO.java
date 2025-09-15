package com.tirana.smartparking.parking.dto;

public record ParkingLotClientResponseDTO(
        long id,
        String name,
        double latitude,
        double longitude,
        Double pricePerHour,
        Double pricePerDay,
        int totalSpaces,
        int availableSpaces,
        String status,
        boolean hasCctv,
        boolean hasDisabledAccess,
        boolean hasChargingStations,
        boolean publicAccess,
        String description,
        String address,
        String source
) {
}
