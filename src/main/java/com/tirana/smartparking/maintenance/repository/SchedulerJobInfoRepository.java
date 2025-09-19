package com.tirana.smartparking.maintenance.repository;

import com.tirana.smartparking.maintenance.entity.SchedulerJobInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchedulerJobInfoRepository extends JpaRepository<SchedulerJobInfo, Long> {
    
    Optional<SchedulerJobInfo> findByJobName(String jobName);
    
    List<SchedulerJobInfo> findByIsActiveTrue();
    
    List<SchedulerJobInfo> findByStatus(SchedulerJobInfo.JobStatus status);
    
    @Query("SELECT s FROM SchedulerJobInfo s WHERE s.isActive = true AND s.nextExecutionTime <= :currentTime")
    List<SchedulerJobInfo> findJobsDueForExecution(@Param("currentTime") ZonedDateTime currentTime);
    
    @Query("SELECT s FROM SchedulerJobInfo s WHERE s.isActive = true ORDER BY s.nextExecutionTime ASC")
    List<SchedulerJobInfo> findActiveJobsOrderByNextExecution();
    
    @Query("SELECT COUNT(s) FROM SchedulerJobInfo s WHERE s.status = :status AND s.isActive = true")
    long countByStatusAndIsActiveTrue(@Param("status") SchedulerJobInfo.JobStatus status);
    
    @Query("SELECT s FROM SchedulerJobInfo s WHERE s.isActive = true AND s.lastExecutionTime IS NOT NULL ORDER BY s.lastExecutionTime DESC")
    Page<SchedulerJobInfo> findRecentlyExecutedJobs(Pageable pageable);
    
    @Query("SELECT s FROM SchedulerJobInfo s WHERE s.isActive = true AND s.lastExecutionStatus = :status ORDER BY s.lastExecutionTime DESC")
    List<SchedulerJobInfo> findByLastExecutionStatus(@Param("status") SchedulerJobInfo.ExecutionStatus status);
}
