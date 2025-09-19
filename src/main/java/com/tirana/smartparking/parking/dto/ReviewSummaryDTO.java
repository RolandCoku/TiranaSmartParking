package com.tirana.smartparking.parking.dto;

import java.time.Instant;

public record ReviewSummaryDTO(
        Long id,
        Long userId,
        String userName,
        Integer rating,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
}
