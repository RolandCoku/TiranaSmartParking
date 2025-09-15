package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.RateRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRuleRepository extends JpaRepository<RateRule, Long> {
    List<RateRule> findByRatePlanId(Long ratePlanId);
    Page<RateRule> findByRatePlanId(Long ratePlanId, Pageable pageable);
}
