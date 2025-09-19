package com.tirana.smartparking.parking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record ParkingSessionStopDTO(
        @NotNull(message = "End time is required")
        @FutureOrPresent(message = "End time must be in the present or future")
        ZonedDateTime endTime,
        
        String notes
) {}
