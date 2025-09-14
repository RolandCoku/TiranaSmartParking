package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.SpaceRateOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRateOverrideRepository extends JpaRepository<SpaceRateOverride, Long> {
}
