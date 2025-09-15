package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.SpaceRateOverride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface SpaceRateOverrideRepository extends JpaRepository<SpaceRateOverride, Long> {
    
    @Query("SELECT sro FROM SpaceRateOverride sro WHERE sro.space.id = :spaceId " +
           "AND (sro.effectiveFrom IS NULL OR sro.effectiveFrom <= :date) " +
           "AND (sro.effectiveTo IS NULL OR sro.effectiveTo > :date) " +
           "ORDER BY sro.priority DESC")
    List<SpaceRateOverride> findActiveOverridesForSpace(@Param("spaceId") Long spaceId, @Param("date") ZonedDateTime date);
    
    Page<SpaceRateOverride> findBySpaceId(Long spaceId, Pageable pageable);
    
    boolean existsBySpaceIdAndRatePlanId(Long spaceId, Long ratePlanId);
}
