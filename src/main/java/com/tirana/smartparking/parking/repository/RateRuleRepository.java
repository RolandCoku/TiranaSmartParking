package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.RateRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRuleRepository extends JpaRepository<RateRule, Long> {
}
