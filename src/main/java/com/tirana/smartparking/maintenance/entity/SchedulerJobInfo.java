package com.tirana.smartparking.maintenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scheduler_job_info", 
       uniqueConstraints = @UniqueConstraint(name = "uk_job_name", columnNames = {"job_name"}))
public class SchedulerJobInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "job_name", nullable = false, unique = true)
    private String jobName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "cron_expression")
    private String cronExpression;
    
    @Column(name = "delay_ms")
    private Long delayMs;
    
    @Column(name = "last_execution_time")
    private ZonedDateTime lastExecutionTime;
    
    @Column(name = "next_execution_time")
    private ZonedDateTime nextExecutionTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status = JobStatus.RUNNING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "last_execution_status")
    private ExecutionStatus lastExecutionStatus = ExecutionStatus.PENDING;
    
    @Column(name = "processed_count")
    private Integer processedCount = 0;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
    
    public enum JobStatus {
        RUNNING, STOPPED, PAUSED, ERROR
    }
    
    public enum ExecutionStatus {
        PENDING, SUCCESS, ERROR, TIMEOUT
    }
}
