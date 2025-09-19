# Production-Ready Scheduler System

## Overview

The production-ready scheduler system provides comprehensive monitoring, execution tracking, and maintenance capabilities for booking management operations. It replaces the in-memory implementation with a robust database-backed solution.

## Architecture

### Core Components

1. **SchedulerJobInfo Entity** - Tracks scheduler job metadata and execution status
2. **MaintenanceExecution Entity** - Records all maintenance operation executions
3. **SchedulerInfoService** - Business logic for scheduler management
4. **BookingScheduler** - Actual scheduled job execution
5. **Database Repositories** - Data persistence layer
6. **Caching Layer** - Performance optimization

### Database Schema

#### scheduler_job_info
- Tracks job configuration, status, and execution history
- Includes retry logic and error handling
- Supports job lifecycle management

#### maintenance_executions
- Records all maintenance operation executions
- Includes timing, status, and error information
- Supports cleanup and retention policies

## Features

### Production Features

#### 1. **Database Persistence**
- All scheduler information stored in PostgreSQL
- ACID compliance for data integrity
- Proper indexing for performance

#### 2. **Error Handling & Resilience**
- Comprehensive exception handling
- Retry logic with configurable limits
- Graceful degradation on failures
- Detailed error logging

#### 3. **Performance Optimization**
- Spring Cache integration
- Database connection pooling
- Optimized queries with proper indexing
- Pagination for large datasets

#### 4. **Monitoring & Observability**
- Structured logging with SLF4J
- Execution timing and metrics
- Server instance tracking
- Operation success/failure rates

#### 5. **Configuration Management**
- Externalized configuration
- Environment-specific settings
- Runtime configuration updates
- Feature toggles

#### 6. **Data Management**
- Automatic cleanup of old records
- Configurable retention policies
- Data archival capabilities
- Backup and recovery support

## Configuration

### Application Properties

```yaml
schedulers:
  activateBookings:
    delayMs: 30000          # 30 seconds
    maxRetries: 3
    timeoutMs: 30000
    enabled: true
  noShows:
    delayMs: 60000          # 60 seconds
    maxRetries: 3
    timeoutMs: 30000
    enabled: true
  maintenance:
    historyRetentionDays: 30
    maxHistoryRecords: 10000
    cleanupEnabled: true
    cleanupIntervalMs: 86400000  # 24 hours
```

### Environment Profiles

- **Development**: `application-dev.yml`
- **Production**: `application-prod.yml`
- **Testing**: `application-test.yml`

## API Endpoints

### Maintenance Operations

#### Manual Execution
```http
POST /api/v1/admin/bookings/maintenance/update-expired
POST /api/v1/admin/bookings/maintenance/update-completed
POST /api/v1/admin/bookings/maintenance/activate-due
POST /api/v1/admin/bookings/maintenance/cancel-no-shows
```

**Response:**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "operation": "update-expired",
    "description": "Update expired bookings",
    "executedAt": "2024-01-15T10:30:00Z",
    "status": "SUCCESS",
    "processedCount": 1,
    "errorMessage": null,
    "executionTimeMs": 150
  }
}
```

### Scheduler Status

#### Get All Jobs
```http
GET /api/v1/admin/bookings/scheduler/status
```

#### Get Specific Job
```http
GET /api/v1/admin/bookings/scheduler/jobs/{jobName}
```

#### Get Execution History
```http
GET /api/v1/admin/bookings/maintenance/history
```

## Monitoring & Alerting

### Key Metrics

1. **Job Execution Success Rate**
2. **Average Execution Time**
3. **Error Frequency**
4. **Retry Count**
5. **Queue Depth**

### Logging

#### Structured Logging
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "level": "INFO",
  "logger": "SchedulerInfoServiceImpl",
  "message": "Completed maintenance operation: update-expired in 150ms",
  "operation": "update-expired",
  "executionTimeMs": 150,
  "status": "SUCCESS"
}
```

#### Error Logging
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "level": "ERROR",
  "logger": "SchedulerInfoServiceImpl",
  "message": "Failed to execute maintenance operation: update-expired",
  "operation": "update-expired",
  "error": "Database connection timeout",
  "stackTrace": "..."
}
```

## Deployment Considerations

### Database Setup

1. **Run Migrations**
   ```bash
   ./mvnw flyway:migrate
   ```

2. **Verify Tables**
   ```sql
   SELECT * FROM scheduler_job_info;
   SELECT * FROM maintenance_executions;
   ```

### Performance Tuning

1. **Connection Pooling**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 20
         minimum-idle: 5
   ```

2. **Cache Configuration**
   ```yaml
   spring:
     cache:
       type: caffeine
       caffeine:
         spec: maximumSize=1000,expireAfterWrite=5m
   ```

### Security

1. **Authentication Required**
   - All endpoints require `BOOKING_READ` or `BOOKING_UPDATE` authority
   - Admin-only access to scheduler information

2. **Input Validation**
   - All inputs validated with Bean Validation
   - SQL injection prevention through JPA

## Troubleshooting

### Common Issues

#### 1. **Job Not Executing**
- Check job status in database
- Verify scheduler is enabled
- Check for errors in logs

#### 2. **High Memory Usage**
- Monitor cache size
- Check for memory leaks in execution history
- Adjust retention policies

#### 3. **Database Performance**
- Monitor query execution times
- Check index usage
- Optimize connection pool settings

### Health Checks

#### Database Connectivity
```http
GET /actuator/health/db
```

#### Scheduler Status
```http
GET /api/v1/admin/bookings/scheduler/status
```

## Maintenance

### Regular Tasks

1. **Monitor Execution History**
   - Review error rates
   - Check execution times
   - Identify performance issues

2. **Database Maintenance**
   - Monitor table sizes
   - Run cleanup operations
   - Update statistics

3. **Configuration Review**
   - Adjust timing based on load
   - Update retry policies
   - Optimize resource usage

### Backup Strategy

1. **Database Backups**
   - Daily full backups
   - Transaction log backups
   - Point-in-time recovery

2. **Configuration Backups**
   - Version control
   - Environment-specific configs
   - Rollback procedures

## Future Enhancements

1. **Distributed Scheduling**
   - Multi-instance coordination
   - Leader election
   - Load balancing

2. **Advanced Monitoring**
   - Prometheus metrics
   - Grafana dashboards
   - Alerting rules

3. **Dynamic Configuration**
   - Runtime config updates
   - Feature flags
   - A/B testing support
