package com.tirana.smartparking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalSpots;
    private long occupiedSpots;
    private long availableSpots;
    private long activeBookings;
    private long activeSessions;
    private double todayRevenue;
    private double occupancyRate;
}
