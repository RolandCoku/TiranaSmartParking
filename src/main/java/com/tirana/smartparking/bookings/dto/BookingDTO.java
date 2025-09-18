package com.tirana.smartparking.bookings.dto;

import com.tirana.smartparking.bookings.entity.Booking;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;

import java.time.Instant;
import java.time.ZonedDateTime;

public record BookingDTO(
        Long id,
        Long userId,
        String userEmail,
        Long parkingSpaceId,
        String parkingSpaceLabel,
        Long parkingLotId,
        String parkingLotName,
        String parkingLotAddress,
        String vehiclePlate,
        VehicleType vehicleType,
        UserGroup userGroup,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        Integer totalPrice,
        String currency,
        Booking.BookingStatus status,
        String bookingReference,
        String paymentMethodId,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
}