package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.DashboardStatsDTO;
import com.tirana.smartparking.user.dto.OccupancyDataDTO;

import java.util.List;

public interface DashboardService {
    DashboardStatsDTO getDashboardStats();
    List<OccupancyDataDTO> getOccupancyData();
}
