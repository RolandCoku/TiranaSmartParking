package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

public interface RateManagementService {
    
    // Rate Plan operations
    RatePlanDTO createRatePlan(RatePlanRegistrationDTO registrationDTO);
    RatePlanDTO getRatePlanById(Long id);
    Page<RatePlanDTO> getAllRatePlans(Pageable pageable);
    RatePlanDTO updateRatePlan(Long id, RatePlanRegistrationDTO registrationDTO);
    void deleteRatePlan(Long id);
    
    // Rate Rule operations
    RateRuleDTO createRateRule(RateRuleRegistrationDTO registrationDTO);
    RateRuleDTO getRateRuleById(Long id);
    Page<RateRuleDTO> getRateRulesByPlanId(Long planId, Pageable pageable);
    Page<RateRuleDTO> getAllRateRules(Pageable pageable);
    RateRuleDTO updateRateRule(Long id, RateRuleRegistrationDTO registrationDTO);
    void deleteRateRule(Long id);
    
    // Lot Rate Assignment operations
    LotRateAssignmentDTO createLotRateAssignment(LotRateAssignmentRegistrationDTO registrationDTO);
    LotRateAssignmentDTO getLotRateAssignmentById(Long id);
    Page<LotRateAssignmentDTO> getLotRateAssignmentsByLotId(Long lotId, Pageable pageable);
    Page<LotRateAssignmentDTO> getAllLotRateAssignments(Pageable pageable);
    LotRateAssignmentDTO updateLotRateAssignment(Long id, LotRateAssignmentRegistrationDTO registrationDTO);
    void deleteLotRateAssignment(Long id);
    
    // Space Rate Override operations
    SpaceRateOverrideDTO createSpaceRateOverride(SpaceRateOverrideRegistrationDTO registrationDTO);
    SpaceRateOverrideDTO getSpaceRateOverrideById(Long id);
    Page<SpaceRateOverrideDTO> getSpaceRateOverridesBySpaceId(Long spaceId, Pageable pageable);
    Page<SpaceRateOverrideDTO> getAllSpaceRateOverrides(Pageable pageable);
    SpaceRateOverrideDTO updateSpaceRateOverride(Long id, SpaceRateOverrideRegistrationDTO registrationDTO);
    void deleteSpaceRateOverride(Long id);
    
    // Pricing operations
    Money getPricingQuote(PricingQuoteDTO quoteDTO);
    
    // Convenience method for standalone space pricing
    Money getStandaloneSpacePricingQuote(Long spaceId, VehicleType vehicleType, UserGroup userGroup, 
                                        ZonedDateTime startTime, ZonedDateTime endTime);
}
