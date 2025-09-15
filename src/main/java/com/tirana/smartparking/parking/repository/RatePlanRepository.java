package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {
}
