package com.tirana.smartparking.parking.sensor.dto;

import java.time.Instant;

public record SensorEventDTO(
        String deviceId,
        String apiKey,
        String event,
        Long spaceId,
        Instant timestamp,
        String sensorType
) {
}
