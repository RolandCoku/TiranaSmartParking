package com.tirana.smartparking.parking.sensor.dto;

import com.tirana.smartparking.parking.sensor.entity.SensorDevice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record SensorRegistrationDTO(
        @NotNull @NotBlank String deviceId,
        @NotNull @NotBlank String apiKey,
        String sensorType,
        Long parkingLotId,
        Long parkingSpaceId,
        String description
) {
    public SensorRegistrationDTO {
        try {
            SensorDevice.SensorType type = SensorDevice.SensorType.valueOf(sensorType);
            if (type != SensorDevice.SensorType.PER_SPACE && type != SensorDevice.SensorType.GATE_COUNTER) {
                throw new IllegalArgumentException("Sensor Type must be PER_SPACE or GATE_COUNTER");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Sensor Type must be PER_SPACE or GATE_COUNTER", e);
        }
    }
}
