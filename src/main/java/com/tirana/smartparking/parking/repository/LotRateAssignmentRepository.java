package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.LotRateAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRateAssignmentRepository extends JpaRepository<LotRateAssignment, Long> {
}
