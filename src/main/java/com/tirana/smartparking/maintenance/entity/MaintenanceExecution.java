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
@Table(name = "maintenance_executions",
       indexes = {
           @Index(name = "idx_execution_time", columnList = "executed_at"),
           @Index(name = "idx_operation", columnList = "operation"),
           @Index(name = "idx_status", columnList = "status")
       })
public class MaintenanceExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "operation", nullable = false, length = 100)
    private String operation;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "executed_at", nullable = false)
    private ZonedDateTime executedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExecutionStatus status;
    
    @Column(name = "processed_count")
    private Integer processedCount = 0;
    
    @Column(name = "error_message", length = 2000)
    private String errorMessage;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "triggered_by", length = 100)
    private String triggeredBy; // SCHEDULER, MANUAL, API
    
    @Column(name = "server_instance", length = 100)
    private String serverInstance;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
    
    public enum ExecutionStatus {
        SUCCESS, ERROR, TIMEOUT, CANCELLED
    }
}
