# Maintenance Module

## Overview

The Maintenance Module provides a centralized, production-ready system for managing scheduled tasks and maintenance operations across all modules in the Smart Parking application. It follows the established module structure and provides a clean separation of concerns.

## Architecture

### Module Structure

```
src/main/java/com/tirana/smartparking/maintenance/
├── controller/
│   ├── MaintenanceController.java          # Generic maintenance endpoints
│   └── README_MAINTENANCE_MODULE.md       # This documentation
├── dto/
│   ├── MaintenanceExecutionDTO.java       # Execution result DTO
│   └── SchedulerJobInfoDTO.java           # Job information DTO
├── entity/
│   ├── MaintenanceExecution.java          # Execution history entity
│   └── SchedulerJobInfo.java              # Job metadata entity
├── repository/
│   ├── MaintenanceExecutionRepository.java # Execution data access
│   └── SchedulerJobInfoRepository.java    # Job data access
├── service/
│   ├── SchedulerInfoService.java          # Service interface
│   └── implementation/
│       └── SchedulerInfoServiceImpl.java # Base service implementation
└── config/
    ├── CacheConfig.java                   # Caching configuration
    └── SchedulerConfig.java               # Scheduler configuration
```

## Features

### 1. **Generic Maintenance Framework**
- Centralized maintenance operation execution
- Cross-module compatibility
- Extensible operation system
- Consistent error handling and logging

### 2. **Production-Ready Scheduler**
- Database-backed job tracking
- Retry logic with configurable limits
- Execution history and monitoring
- Server instance tracking

### 3. **Module Integration**
- Each module can provide its own maintenance operations
- Delegation pattern for operation routing
- Consistent API across all modules
- Centralized configuration management

## Usage

### For Module Developers

#### 1. **Add Module Operations to Maintenance Service**

To add new maintenance operations for your module, simply update the `SchedulerInfoServiceImpl` in the maintenance module:

```java
// In SchedulerInfoServiceImpl.java

private boolean isYourModuleOperation(String operation) {
    return operation.toLowerCase().matches("(your-operation-1|your-operation-2)");
}

private void executeYourModuleOperation(String operation) {
    switch (operation.toLowerCase()) {
        case "your-operation-1":
            yourModuleService.performOperation1();
            break;
        case "your-operation-2":
            yourModuleService.performOperation2();
            break;
        default:
            throw new IllegalArgumentException("Unknown your-module operation: " + operation);
    }
}

// Update the main executeMaintenanceOperation method
@Override
public MaintenanceExecutionDTO executeMaintenanceOperation(String operation) {
    // ... existing code ...
    
    if (isBookingOperation(operation)) {
        executeBookingOperation(operation);
    } else if (isYourModuleOperation(operation)) {
        executeYourModuleOperation(operation);
    } else {
        logger.warn("Unknown maintenance operation: {}", operation);
    }
    
    // ... rest of the method ...
}
```

#### 2. **Create Module-Specific Controller**

```java
@RestController
@RequestMapping("/api/v1/admin/your-module/maintenance")
public class YourModuleMaintenanceController {
    
    private final SchedulerInfoService schedulerInfoService;
    
    public YourModuleMaintenanceController(SchedulerInfoService schedulerInfoService) {
        this.schedulerInfoService = schedulerInfoService;
    }
    
    @PreAuthorize("hasAuthority('YOUR_MODULE_UPDATE')")
    @PostMapping("/your-operation-1")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> executeOperation1() {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation("your-operation-1");
        return ResponseHelper.ok("Operation 1 executed successfully", execution);
    }
    
    @PreAuthorize("hasAuthority('YOUR_MODULE_UPDATE')")
    @PostMapping("/your-operation-2")
    public ResponseEntity<ApiResponse<MaintenanceExecutionDTO>> executeOperation2() {
        MaintenanceExecutionDTO execution = schedulerInfoService.executeMaintenanceOperation("your-operation-2");
        return ResponseHelper.ok("Operation 2 executed successfully", execution);
    }
}
```

#### 3. **Create Scheduled Jobs**

```java
@Component
@RequiredArgsConstructor
public class YourModuleScheduler {
    private final YourModuleService yourModuleService;
    private final SchedulerInfoService schedulerInfoService;

    @Scheduled(fixedDelayString = "${schedulers.yourModule.delayMs:60000}")
    public void yourScheduledTask() {
        long startTime = System.currentTimeMillis();
        String status = "SUCCESS";
        Integer processedCount = 0;
        String errorMessage = null;
        
        try {
            yourModuleService.performMaintenanceTask();
            processedCount = 1;
        } catch (Exception e) {
            status = "ERROR";
            errorMessage = e.getMessage();
        }
        
        long executionTimeMs = System.currentTimeMillis() - startTime;
        schedulerInfoService.recordJobExecution("yourScheduledTask", status, processedCount, errorMessage, executionTimeMs);
    }
}
```

### For System Administrators

#### 1. **Generic Maintenance Endpoints**

```http
# Get scheduler status
GET /api/v1/maintenance/scheduler/status

# Get specific job status
GET /api/v1/maintenance/scheduler/jobs/{jobName}

# Get execution history
GET /api/v1/maintenance/executions/history

# Execute any maintenance operation
POST /api/v1/maintenance/execute/{operation}
```

#### 2. **Module-Specific Endpoints**

```http
# Booking maintenance operations
POST /api/v1/admin/bookings/maintenance/update-expired
POST /api/v1/admin/bookings/maintenance/update-completed
POST /api/v1/admin/bookings/maintenance/activate-due
POST /api/v1/admin/bookings/maintenance/cancel-no-shows

# Future module operations
POST /api/v1/admin/parking/maintenance/cleanup-sessions
POST /api/v1/admin/users/maintenance/cleanup-inactive-users
POST /api/v1/admin/notifications/maintenance/send-pending-notifications
```

## Configuration

### Application Properties

```yaml
schedulers:
  activateBookings:
    delayMs: 30000
    maxRetries: 3
    timeoutMs: 30000
    enabled: true
  noShows:
    delayMs: 60000
    maxRetries: 3
    timeoutMs: 30000
    enabled: true
  maintenance:
    historyRetentionDays: 30
    maxHistoryRecords: 10000
    cleanupEnabled: true
    cleanupIntervalMs: 86400000
```

### Database Schema

The maintenance module creates two tables:

#### scheduler_job_info
- Tracks job metadata and execution status
- Includes retry logic and error handling
- Supports job lifecycle management

#### maintenance_executions
- Records all maintenance operation executions
- Includes timing, status, and error information
- Supports cleanup and retention policies

## Benefits

### 1. **Separation of Concerns**
- Maintenance logic separated from business logic
- Centralized scheduling and monitoring
- Consistent error handling across modules

### 2. **Scalability**
- Easy to add new maintenance operations
- Support for multiple modules
- Horizontal scaling support

### 3. **Maintainability**
- Single source of truth for maintenance operations
- Consistent API patterns
- Centralized configuration

### 4. **Monitoring**
- Unified execution history
- Cross-module performance metrics
- Centralized error tracking

## Integration with Booking Module

The maintenance module is fully integrated with the booking module:

1. **SchedulerInfoServiceImpl** - Handles all booking maintenance operations directly
2. **BookingMaintenanceController** - Provides booking maintenance endpoints that delegate to the maintenance service
3. **BookingScheduler** - Uses the maintenance service for job tracking

This approach centralizes all maintenance logic in the maintenance module while keeping module-specific endpoints in their respective modules for better organization.

## Future Enhancements

1. **Distributed Scheduling** - Multi-instance coordination
2. **Advanced Monitoring** - Prometheus metrics integration
3. **Dynamic Configuration** - Runtime configuration updates
4. **Operation Templates** - Predefined operation patterns
5. **Dependency Management** - Operation execution ordering
