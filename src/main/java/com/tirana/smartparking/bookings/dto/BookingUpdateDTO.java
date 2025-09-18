package com.tirana.smartparking.bookings.dto;

import com.tirana.smartparking.bookings.entity.Booking;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.validation.constraints.Future;

import java.time.ZonedDateTime;

public record BookingUpdateDTO(
        String vehiclePlate,
        VehicleType vehicleType,
        UserGroup userGroup,
        @Future(message = "Start time must be in the future")
        ZonedDateTime startTime,
        @Future(message = "End time must be in the future")
        ZonedDateTime endTime,
        Booking.BookingStatus status,
        String paymentMethodId,
        String notes
) {
}