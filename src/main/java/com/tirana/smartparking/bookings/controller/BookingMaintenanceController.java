package com.tirana.smartparking.bookings.controller;

import com.tirana.smartparking.maintenance.dto.MaintenanceExecutionDTO;
import com.tirana.smartparking.maintenance.service.SchedulerInfoService;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/bookings/maintenance")
public class BookingMaintenanceController {
    
    private final SchedulerInfoService schedulerInfoService;
    
    public BookingMaintenanceController(SchedulerInfoService schedulerInfoService) {
        this.schedulerInfoService = schedulerInfoService;
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/update-expired")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> updateExpiredBookings() {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation("update-expired");
        return ResponseHelper.ok("Expired bookings updated successfully", execution);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/update-completed")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> updateCompletedBookings() {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation("update-completed");
        return ResponseHelper.ok("Completed bookings updated successfully", execution);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/activate-due")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> activateDueBookings() {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation("activate-due");
        return ResponseHelper.ok("Due bookings activated successfully", execution);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/cancel-no-shows")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> cancelNoShows() {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation("cancel-no-shows");
        return ResponseHelper.ok("No-show bookings cancelled successfully", execution);
    }
}
