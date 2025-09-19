package com.tirana.smartparking.maintenance.repository;

import com.tirana.smartparking.maintenance.entity.MaintenanceExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MaintenanceExecutionRepository extends JpaRepository<MaintenanceExecution, Long> {
    
    Page<MaintenanceExecution> findByOperationOrderByExecutedAtDesc(String operation, Pageable pageable);
    
    Page<MaintenanceExecution> findByStatusOrderByExecutedAtDesc(MaintenanceExecution.ExecutionStatus status, Pageable pageable);
    
    Page<MaintenanceExecution> findByTriggeredByOrderByExecutedAtDesc(String triggeredBy, Pageable pageable);
    
    @Query("SELECT m FROM MaintenanceExecution m ORDER BY m.executedAt DESC")
    Page<MaintenanceExecution> findAllOrderByExecutedAtDesc(Pageable pageable);
    
    @Query("SELECT m FROM MaintenanceExecution m WHERE m.executedAt >= :fromDate AND m.executedAt <= :toDate ORDER BY m.executedAt DESC")
    Page<MaintenanceExecution> findByExecutedAtBetween(@Param("fromDate") ZonedDateTime fromDate, 
                                                       @Param("toDate") ZonedDateTime toDate, 
                                                       Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM MaintenanceExecution m WHERE m.status = :status AND m.executedAt >= :fromDate")
    long countByStatusAndExecutedAtAfter(@Param("status") MaintenanceExecution.ExecutionStatus status, 
                                        @Param("fromDate") ZonedDateTime fromDate);
    
    @Query("SELECT m FROM MaintenanceExecution m WHERE m.operation = :operation AND m.status = :status ORDER BY m.executedAt DESC")
    List<MaintenanceExecution> findByOperationAndStatus(@Param("operation") String operation, 
                                                        @Param("status") MaintenanceExecution.ExecutionStatus status);
    
    @Query("SELECT m FROM MaintenanceExecution m WHERE m.executedAt < :cutoffDate")
    List<MaintenanceExecution> findOlderThan(@Param("cutoffDate") ZonedDateTime cutoffDate);
    
    @Query("SELECT m.operation, COUNT(m), AVG(m.executionTimeMs) FROM MaintenanceExecution m WHERE m.executedAt >= :fromDate GROUP BY m.operation")
    List<Object[]> getExecutionStatistics(@Param("fromDate") ZonedDateTime fromDate);
}
