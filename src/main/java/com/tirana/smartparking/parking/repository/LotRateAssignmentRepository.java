package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.LotRateAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface LotRateAssignmentRepository extends JpaRepository<LotRateAssignment, Long> {
    
    @Query("SELECT lra FROM LotRateAssignment lra WHERE lra.lot.id = :lotId " +
           "AND (lra.effectiveFrom IS NULL OR lra.effectiveFrom <= :date) " +
           "AND (lra.effectiveTo IS NULL OR lra.effectiveTo > :date) " +
           "ORDER BY lra.priority DESC")
    List<LotRateAssignment> findActiveAssignmentsForLot(@Param("lotId") Long lotId, @Param("date") ZonedDateTime date);
    
    Page<LotRateAssignment> findByLotId(Long lotId, Pageable pageable);
    
    boolean existsByLotIdAndRatePlanId(Long lotId, Long ratePlanId);
}
