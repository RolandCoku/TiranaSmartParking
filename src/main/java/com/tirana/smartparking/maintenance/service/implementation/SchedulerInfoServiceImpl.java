package com.tirana.smartparking.maintenance.service.implementation;

import com.tirana.smartparking.maintenance.config.SchedulerConfig;
import com.tirana.smartparking.maintenance.dto.MaintenanceExecutionDTO;
import com.tirana.smartparking.maintenance.dto.SchedulerJobInfoDTO;
import com.tirana.smartparking.maintenance.entity.MaintenanceExecution;
import com.tirana.smartparking.maintenance.entity.SchedulerJobInfo;
import com.tirana.smartparking.maintenance.repository.MaintenanceExecutionRepository;
import com.tirana.smartparking.maintenance.repository.SchedulerJobInfoRepository;
import com.tirana.smartparking.maintenance.service.SchedulerInfoService;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.bookings.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SchedulerInfoServiceImpl implements SchedulerInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerInfoServiceImpl.class);
    
    private final SchedulerJobInfoRepository schedulerJobInfoRepository;
    private final MaintenanceExecutionRepository maintenanceExecutionRepository;
    private final SchedulerConfig schedulerConfig;
    private final BookingService bookingService;
    private final String serverInstance;
    
    public SchedulerInfoServiceImpl(SchedulerJobInfoRepository schedulerJobInfoRepository,
                                   MaintenanceExecutionRepository maintenanceExecutionRepository,
                                   SchedulerConfig schedulerConfig,
                                   BookingService bookingService) {
        this.schedulerJobInfoRepository = schedulerJobInfoRepository;
        this.maintenanceExecutionRepository = maintenanceExecutionRepository;
        this.schedulerConfig = schedulerConfig;
        this.bookingService = bookingService;
        this.serverInstance = getServerInstance();
        initializeJobInfo();
    }
    
    private void initializeJobInfo() {
        try {
            initializeJobIfNotExists("activateDueBookings", 
                    "Activates bookings whose start time has arrived",
                    schedulerConfig.getActivateBookings().getDelayMs(),
                    schedulerConfig.getActivateBookings().getMaxRetries());
            
            initializeJobIfNotExists("cancelNoShows",
                    "Cancels bookings that are past their grace period",
                    schedulerConfig.getNoShows().getDelayMs(),
                    schedulerConfig.getNoShows().getMaxRetries());
            
            logger.info("Scheduler job info initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize scheduler job info", e);
        }
    }
    
    private void initializeJobIfNotExists(String jobName, String description, Long delayMs, Integer maxRetries) {
        Optional<SchedulerJobInfo> existingJob = schedulerJobInfoRepository.findByJobName(jobName);
        if (existingJob.isEmpty()) {
            SchedulerJobInfo jobInfo = new SchedulerJobInfo();
            jobInfo.setJobName(jobName);
            jobInfo.setDescription(description);
            jobInfo.setDelayMs(delayMs);
            jobInfo.setMaxRetries(maxRetries);
            jobInfo.setNextExecutionTime(ZonedDateTime.now().plusSeconds(delayMs / 1000));
            jobInfo.setStatus(SchedulerJobInfo.JobStatus.RUNNING);
            jobInfo.setIsActive(true);
            
            schedulerJobInfoRepository.save(jobInfo);
            logger.info("Created new scheduler job: {}", jobName);
        }
    }
    
    private String getServerInstance() {
        try {
            return InetAddress.getLocalHost().getHostName() + "-" + System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown-" + System.currentTimeMillis();
        }
    }
    
    @Override
    @Cacheable(value = "schedulerJobs", key = "'all'")
    public List<SchedulerJobInfoDTO> getSchedulerJobInfo() {
        try {
            List<SchedulerJobInfo> jobs = schedulerJobInfoRepository.findByIsActiveTrue();
            return jobs.stream()
                    .map(this::mapToSchedulerJobInfoDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve scheduler job info", e);
            throw new RuntimeException("Failed to retrieve scheduler job info", e);
        }
    }
    
    @Override
    @Cacheable(value = "schedulerJobs", key = "#jobName")
    public SchedulerJobInfoDTO getJobInfo(String jobName) {
        try {
            Optional<SchedulerJobInfo> jobInfo = schedulerJobInfoRepository.findByJobName(jobName);
            if (jobInfo.isEmpty()) {
                throw new ResourceNotFoundException("Job not found: " + jobName);
            }
            return mapToSchedulerJobInfoDTO(jobInfo.get());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to retrieve job info for: {}", jobName, e);
            throw new RuntimeException("Failed to retrieve job info", e);
        }
    }
    
    @Override
    public List<MaintenanceExecutionDTO> getMaintenanceExecutionHistory() {
        try {
            Pageable pageable = PageRequest.of(0, 100); // Limit to last 100 executions
            Page<MaintenanceExecution> executions = maintenanceExecutionRepository.findAllOrderByExecutedAtDesc(pageable);
            return executions.getContent().stream()
                    .map(this::mapToMaintenanceExecutionDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve maintenance execution history", e);
            throw new RuntimeException("Failed to retrieve maintenance execution history", e);
        }
    }
    
    @Override
    @CacheEvict(value = "schedulerJobs", allEntries = true)
    public MaintenanceExecutionDTO executeMaintenanceOperation(String operation) {
        long startTime = System.currentTimeMillis();
        ZonedDateTime executedAt = ZonedDateTime.now();
        MaintenanceExecution.ExecutionStatus status = MaintenanceExecution.ExecutionStatus.SUCCESS;
        Integer processedCount = 0;
        String errorMessage = null;
        
        try {
            logger.info("Starting maintenance operation: {}", operation);
            
            // Handle booking-specific operations
            if (isBookingOperation(operation)) {
                executeBookingOperation(operation);
            } else {
                // Handle other module operations here in the future
                logger.warn("Unknown maintenance operation: {}", operation);
            }
            
            processedCount = 1;
            
            logger.info("Completed maintenance operation: {} in {}ms", operation, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            status = MaintenanceExecution.ExecutionStatus.ERROR;
            errorMessage = e.getMessage();
            logger.error("Failed to execute maintenance operation: {}", operation, e);
        }
        
        long executionTimeMs = System.currentTimeMillis() - startTime;
        
        // Save execution record to database
        MaintenanceExecution execution = new MaintenanceExecution();
        execution.setOperation(operation);
        execution.setDescription(getOperationDescription(operation));
        execution.setExecutedAt(executedAt);
        execution.setStatus(status);
        execution.setProcessedCount(processedCount);
        execution.setErrorMessage(errorMessage);
        execution.setExecutionTimeMs(executionTimeMs);
        execution.setTriggeredBy("MANUAL");
        execution.setServerInstance(serverInstance);
        
        MaintenanceExecution savedExecution = maintenanceExecutionRepository.save(execution);
        
        return mapToMaintenanceExecutionDTO(savedExecution);
    }
    
    private boolean isBookingOperation(String operation) {
        return operation.toLowerCase().matches("(update-expired|update-completed|activate-due|cancel-no-shows)");
    }
    
    private void executeBookingOperation(String operation) {
        switch (operation.toLowerCase()) {
            case "update-expired":
                bookingService.updateExpiredBookings();
                break;
            case "update-completed":
                bookingService.updateCompletedBookings();
                break;
            case "activate-due":
                bookingService.activateDueBookings();
                break;
            case "cancel-no-shows":
                bookingService.cancelNoShows();
                break;
            default:
                throw new IllegalArgumentException("Unknown booking maintenance operation: " + operation);
        }
    }
    
    @Override
    @CacheEvict(value = "schedulerJobs", key = "#jobName")
    public void recordJobExecution(String jobName, String status, Integer processedCount, String errorMessage, Long executionTimeMs) {
        try {
            Optional<SchedulerJobInfo> jobInfoOpt = schedulerJobInfoRepository.findByJobName(jobName);
            if (jobInfoOpt.isPresent()) {
                SchedulerJobInfo jobInfo = jobInfoOpt.get();
                ZonedDateTime now = ZonedDateTime.now();
                
                jobInfo.setLastExecutionTime(now);
                jobInfo.setNextExecutionTime(now.plusSeconds(jobInfo.getDelayMs() / 1000));
                jobInfo.setLastExecutionStatus(mapToExecutionStatus(status));
                jobInfo.setProcessedCount(processedCount);
                jobInfo.setErrorMessage(errorMessage);
                
                if ("ERROR".equals(status)) {
                    jobInfo.setRetryCount(jobInfo.getRetryCount() + 1);
                    if (jobInfo.getRetryCount() >= jobInfo.getMaxRetries()) {
                        jobInfo.setStatus(SchedulerJobInfo.JobStatus.ERROR);
                    }
                } else {
                    jobInfo.setRetryCount(0);
                    jobInfo.setStatus(SchedulerJobInfo.JobStatus.RUNNING);
                }
                
                schedulerJobInfoRepository.save(jobInfo);
                
                // Also record in maintenance execution history
                MaintenanceExecution execution = new MaintenanceExecution();
                execution.setOperation(jobName);
                execution.setDescription(jobInfo.getDescription());
                execution.setExecutedAt(now);
                execution.setStatus(mapToMaintenanceExecutionStatus(status));
                execution.setProcessedCount(processedCount);
                execution.setErrorMessage(errorMessage);
                execution.setExecutionTimeMs(executionTimeMs);
                execution.setTriggeredBy("SCHEDULER");
                execution.setServerInstance(serverInstance);
                
                maintenanceExecutionRepository.save(execution);
                
                logger.debug("Recorded execution for job: {} with status: {}", jobName, status);
            }
        } catch (Exception e) {
            logger.error("Failed to record job execution for: {}", jobName, e);
        }
    }
    
    @Scheduled(fixedDelayString = "${schedulers.maintenance.cleanupIntervalMs:86400000}")
    public void cleanupOldExecutions() {
        if (!schedulerConfig.getMaintenance().getCleanupEnabled()) {
            return;
        }
        
        try {
            ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(schedulerConfig.getMaintenance().getHistoryRetentionDays());
            List<MaintenanceExecution> oldExecutions = maintenanceExecutionRepository.findOlderThan(cutoffDate);
            
            if (!oldExecutions.isEmpty()) {
                maintenanceExecutionRepository.deleteAll(oldExecutions);
                logger.info("Cleaned up {} old maintenance execution records", oldExecutions.size());
            }
        } catch (Exception e) {
            logger.error("Failed to cleanup old maintenance executions", e);
        }
    }
    
    private SchedulerJobInfoDTO mapToSchedulerJobInfoDTO(SchedulerJobInfo jobInfo) {
        return new SchedulerJobInfoDTO(
                jobInfo.getJobName(),
                jobInfo.getDescription(),
                jobInfo.getCronExpression(),
                jobInfo.getDelayMs(),
                jobInfo.getLastExecutionTime(),
                jobInfo.getNextExecutionTime(),
                jobInfo.getStatus().name(),
                jobInfo.getLastExecutionStatus().name(),
                jobInfo.getProcessedCount(),
                jobInfo.getErrorMessage()
        );
    }
    
    private MaintenanceExecutionDTO mapToMaintenanceExecutionDTO(MaintenanceExecution execution) {
        return new MaintenanceExecutionDTO(
                execution.getOperation(),
                execution.getDescription(),
                execution.getExecutedAt(),
                execution.getStatus().name(),
                execution.getProcessedCount(),
                execution.getErrorMessage(),
                execution.getExecutionTimeMs()
        );
    }
    
    private SchedulerJobInfo.ExecutionStatus mapToExecutionStatus(String status) {
        return switch (status.toUpperCase()) {
            case "SUCCESS" -> SchedulerJobInfo.ExecutionStatus.SUCCESS;
            case "ERROR" -> SchedulerJobInfo.ExecutionStatus.ERROR;
            case "TIMEOUT" -> SchedulerJobInfo.ExecutionStatus.TIMEOUT;
            default -> SchedulerJobInfo.ExecutionStatus.PENDING;
        };
    }
    
    private MaintenanceExecution.ExecutionStatus mapToMaintenanceExecutionStatus(String status) {
        return switch (status.toUpperCase()) {
            case "SUCCESS" -> MaintenanceExecution.ExecutionStatus.SUCCESS;
            case "ERROR" -> MaintenanceExecution.ExecutionStatus.ERROR;
            case "TIMEOUT" -> MaintenanceExecution.ExecutionStatus.TIMEOUT;
            case "CANCELLED" -> MaintenanceExecution.ExecutionStatus.CANCELLED;
            default -> MaintenanceExecution.ExecutionStatus.SUCCESS;
        };
    }
    
    private String getOperationDescription(String operation) {
        return switch (operation.toLowerCase()) {
            case "update-expired" -> "Update expired bookings";
            case "update-completed" -> "Update completed bookings";
            case "activate-due" -> "Activate due bookings";
            case "cancel-no-shows" -> "Cancel no-show bookings";
            default -> "Unknown operation";
        };
    }
}
