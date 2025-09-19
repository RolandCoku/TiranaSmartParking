package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.user.dto.DashboardStatsDTO;
import com.tirana.smartparking.user.dto.OccupancyDataDTO;
import com.tirana.smartparking.user.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAuthority('DASHBOARD_READ')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseHelper.ok("Dashboard statistics retrieved successfully", stats);
    }

    @PreAuthorize("hasAuthority('DASHBOARD_READ')")
    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<List<OccupancyDataDTO>>> getOccupancyData() {
        List<OccupancyDataDTO> occupancyData = dashboardService.getOccupancyData();
        return ResponseHelper.ok("Occupancy data retrieved successfully", occupancyData);
    }
}
