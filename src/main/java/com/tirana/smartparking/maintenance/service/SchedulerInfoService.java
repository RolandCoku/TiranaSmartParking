package com.tirana.smartparking.maintenance.service;

import com.tirana.smartparking.maintenance.dto.MaintenanceExecutionDTO;
import com.tirana.smartparking.maintenance.dto.SchedulerJobInfoDTO;

import java.util.List;

public interface SchedulerInfoService {
    
    List<SchedulerJobInfoDTO> getSchedulerJobInfo();
    
    SchedulerJobInfoDTO getJobInfo(String jobName);
    
    List<MaintenanceExecutionDTO> getMaintenanceExecutionHistory();
    
    MaintenanceExecutionDTO executeMaintenanceOperation(String operation);
    
    void recordJobExecution(String jobName, String status, Integer processedCount, String errorMessage, Long executionTimeMs);
}
