package com.tirana.smartparking.parking.sensor.dto;

import java.time.Instant;

public record SensorDTO(
        Long id,
        String deviceId,
        String apiKey,
        Long parkingLotId,
        Long spaceId,
        String sensorType,
        String status,
        String description,
        Instant lastSeen,
        String firmware,
        int batteryLevel,
        Instant createdAt,
        Instant updatedAt
) {}