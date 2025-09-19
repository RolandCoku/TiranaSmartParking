package com.tirana.smartparking.maintenance.controller;

import com.tirana.smartparking.maintenance.dto.MaintenanceExecutionDTO;
import com.tirana.smartparking.maintenance.dto.SchedulerJobInfoDTO;
import com.tirana.smartparking.maintenance.service.SchedulerInfoService;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {
    
    private final SchedulerInfoService schedulerInfoService;
    
    public MaintenanceController(SchedulerInfoService schedulerInfoService) {
        this.schedulerInfoService = schedulerInfoService;
    }
    
    // Scheduler status endpoints
    
    @PreAuthorize("hasAuthority('MAINTENANCE_READ')")
    @GetMapping("/scheduler/status")
    public ResponseEntity<ApiResponse<List<SchedulerJobInfoDTO>>> getSchedulerStatus() {
        List<SchedulerJobInfoDTO> schedulerInfo = schedulerInfoService.getSchedulerJobInfo();
        return ResponseHelper.ok("Scheduler status retrieved successfully", schedulerInfo);
    }
    
    @PreAuthorize("hasAuthority('MAINTENANCE_READ')")
    @GetMapping("/scheduler/jobs/{jobName}")
    public ResponseEntity<ApiResponse<SchedulerJobInfoDTO>> getJobStatus(@PathVariable String jobName) {
        SchedulerJobInfoDTO jobInfo = schedulerInfoService.getJobInfo(jobName);
        if (jobInfo == null) {
            return ResponseHelper.notFound("Job not found: " + jobName, null);
        }
        return ResponseHelper.ok("Job status retrieved successfully", jobInfo);
    }
    
    @PreAuthorize("hasAuthority('MAINTENANCE_READ')")
    @GetMapping("/executions/history")
    public ResponseEntity<ApiResponse<List<MaintenanceExecutionDTO>>> getMaintenanceHistory() {
        List<MaintenanceExecutionDTO> history = schedulerInfoService.getMaintenanceExecutionHistory();
        return ResponseHelper.ok("Maintenance execution history retrieved successfully", history);
    }
    
    // Generic maintenance operation endpoint
    @PreAuthorize("hasAuthority('MAINTENANCE_EXECUTE')")
    @PostMapping("/execute/{operation}")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> executeMaintenanceOperation(@PathVariable String operation) {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation(operation);
        return ResponseHelper.ok("Maintenance operation executed successfully", execution);
    }
}
