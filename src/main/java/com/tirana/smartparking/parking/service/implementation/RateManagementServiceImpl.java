package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.parking.entity.LotRateAssignment;
import com.tirana.smartparking.parking.entity.RatePlan;
import com.tirana.smartparking.parking.entity.RateRule;
import com.tirana.smartparking.parking.entity.SpaceRateOverride;
import com.tirana.smartparking.parking.repository.LotRateAssignmentRepository;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.repository.RatePlanRepository;
import com.tirana.smartparking.parking.repository.RateRuleRepository;
import com.tirana.smartparking.parking.repository.SpaceRateOverrideRepository;
import com.tirana.smartparking.parking.service.PricingService;
import com.tirana.smartparking.parking.service.RateManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Service
@Transactional
public class RateManagementServiceImpl implements RateManagementService {
    
    private final RatePlanRepository ratePlanRepository;
    private final RateRuleRepository rateRuleRepository;
    private final LotRateAssignmentRepository lotRateAssignmentRepository;
    private final SpaceRateOverrideRepository spaceRateOverrideRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final PricingService pricingService;
    
    public RateManagementServiceImpl(
            RatePlanRepository ratePlanRepository,
            RateRuleRepository rateRuleRepository,
            LotRateAssignmentRepository lotRateAssignmentRepository,
            SpaceRateOverrideRepository spaceRateOverrideRepository,
            ParkingLotRepository parkingLotRepository,
            ParkingSpaceRepository parkingSpaceRepository,
            PricingService pricingService) {
        this.ratePlanRepository = ratePlanRepository;
        this.rateRuleRepository = rateRuleRepository;
        this.lotRateAssignmentRepository = lotRateAssignmentRepository;
        this.spaceRateOverrideRepository = spaceRateOverrideRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.pricingService = pricingService;
    }
    
    // Rate Plan operations
    @Override
    public RatePlanDTO createRatePlan(RatePlanRegistrationDTO registrationDTO) {
        RatePlan ratePlan = new RatePlan();
        return getRatePlanDTO(registrationDTO, ratePlan);
    }

    private RatePlanDTO getRatePlanDTO(RatePlanRegistrationDTO registrationDTO, RatePlan ratePlan) {
        ratePlan.setName(registrationDTO.name());
        ratePlan.setType(registrationDTO.type());
        ratePlan.setCurrency(registrationDTO.currency());
        ratePlan.setTimeZone(registrationDTO.timeZone());
        ratePlan.setGraceMinutes(registrationDTO.graceMinutes());
        ratePlan.setIncrementMinutes(registrationDTO.incrementMinutes());
        ratePlan.setDailyCap(registrationDTO.dailyCap());
        ratePlan.setActive(registrationDTO.active() != null ? registrationDTO.active() : true);

        RatePlan saved = ratePlanRepository.save(ratePlan);
        return mapToRatePlanDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RatePlanDTO getRatePlanById(Long id) {
        RatePlan ratePlan = ratePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rate plan not found with id: " + id));
        return mapToRatePlanDTO(ratePlan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RatePlanDTO> getAllRatePlans(Pageable pageable) {
        return ratePlanRepository.findAll(pageable)
                .map(this::mapToRatePlanDTO);
    }
    
    @Override
    public RatePlanDTO updateRatePlan(Long id, RatePlanRegistrationDTO registrationDTO) {
        RatePlan ratePlan = ratePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rate plan not found with id: " + id));

        return getRatePlanDTO(registrationDTO, ratePlan);
    }
    
    @Override
    public void deleteRatePlan(Long id) {
        if (!ratePlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rate plan not found with id: " + id);
        }
        ratePlanRepository.deleteById(id);
    }
    
    // Rate Rule operations
    @Override
    public RateRuleDTO createRateRule(RateRuleRegistrationDTO registrationDTO) {
        RateRule rateRule = new RateRule();
        return getRateRuleDTO(registrationDTO, rateRule);
    }

    private RateRuleDTO getRateRuleDTO(RateRuleRegistrationDTO registrationDTO, RateRule rateRule) {
        rateRule.setRatePlan(ratePlanRepository.findById(registrationDTO.ratePlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Rate plan not found with id: " + registrationDTO.ratePlanId())));
        rateRule.setStartMinute(registrationDTO.startMinute());
        rateRule.setEndMinute(registrationDTO.endMinute());
        rateRule.setStartTime(registrationDTO.startTime());
        rateRule.setEndTime(registrationDTO.endTime());
        rateRule.setDayOfWeek(registrationDTO.dayOfWeek());
        rateRule.setVehicleType(registrationDTO.vehicleType());
        rateRule.setUserGroup(registrationDTO.userGroup());
        rateRule.setPricePerHour(registrationDTO.pricePerHour());
        rateRule.setPriceFlat(registrationDTO.priceFlat());

        RateRule saved = rateRuleRepository.save(rateRule);
        return mapToRateRuleDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RateRuleDTO getRateRuleById(Long id) {
        RateRule rateRule = rateRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rate rule not found with id: " + id));
        return mapToRateRuleDTO(rateRule);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RateRuleDTO> getRateRulesByPlanId(Long planId, Pageable pageable) {
        return rateRuleRepository.findByRatePlanId(planId, pageable)
                .map(this::mapToRateRuleDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RateRuleDTO> getAllRateRules(Pageable pageable) {
        return rateRuleRepository.findAll(pageable)
                .map(this::mapToRateRuleDTO);
    }
    
    @Override
    public RateRuleDTO updateRateRule(Long id, RateRuleRegistrationDTO registrationDTO) {
        RateRule rateRule = rateRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rate rule not found with id: " + id));

        return getRateRuleDTO(registrationDTO, rateRule);
    }
    
    @Override
    public void deleteRateRule(Long id) {
        if (!rateRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rate rule not found with id: " + id);
        }
        rateRuleRepository.deleteById(id);
    }
    
    // Lot Rate Assignment operations
    @Override
    public LotRateAssignmentDTO createLotRateAssignment(LotRateAssignmentRegistrationDTO registrationDTO) {
        // Check if the assignment already exists
        if (lotRateAssignmentRepository.existsByLotIdAndRatePlanId(
                registrationDTO.parkingLotId(), registrationDTO.ratePlanId())) {
            throw new ResourceConflictException(
                "Lot rate assignment already exists for lot " + registrationDTO.parkingLotId() + 
                " and rate plan " + registrationDTO.ratePlanId());
        }
        
        LotRateAssignment assignment = new LotRateAssignment();
        return getLotRateAssignmentDTO(registrationDTO, assignment);
    }

    private LotRateAssignmentDTO getLotRateAssignmentDTO(LotRateAssignmentRegistrationDTO registrationDTO, LotRateAssignment assignment) {
        assignment.setLot(parkingLotRepository.findById(registrationDTO.parkingLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + registrationDTO.parkingLotId())));
        assignment.setRatePlan(ratePlanRepository.findById(registrationDTO.ratePlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Rate plan not found with id: " + registrationDTO.ratePlanId())));
        assignment.setPriority(registrationDTO.priority() != null ? registrationDTO.priority() : 0);
        assignment.setEffectiveFrom(registrationDTO.effectiveFrom());
        assignment.setEffectiveTo(registrationDTO.effectiveTo());

        LotRateAssignment saved = lotRateAssignmentRepository.save(assignment);
        return mapToLotRateAssignmentDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LotRateAssignmentDTO getLotRateAssignmentById(Long id) {
        LotRateAssignment assignment = lotRateAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot rate assignment not found with id: " + id));
        return mapToLotRateAssignmentDTO(assignment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LotRateAssignmentDTO> getLotRateAssignmentsByLotId(Long lotId, Pageable pageable) {
        return lotRateAssignmentRepository.findByLotId(lotId, pageable)
                .map(this::mapToLotRateAssignmentDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LotRateAssignmentDTO> getAllLotRateAssignments(Pageable pageable) {
        return lotRateAssignmentRepository.findAll(pageable)
                .map(this::mapToLotRateAssignmentDTO);
    }
    
    @Override
    public LotRateAssignmentDTO updateLotRateAssignment(Long id, LotRateAssignmentRegistrationDTO registrationDTO) {
        LotRateAssignment assignment = lotRateAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot rate assignment not found with id: " + id));

        return getLotRateAssignmentDTO(registrationDTO, assignment);
    }
    
    @Override
    public void deleteLotRateAssignment(Long id) {
        if (!lotRateAssignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lot rate assignment not found with id: " + id);
        }
        lotRateAssignmentRepository.deleteById(id);
    }
    
    // Space Rate Override operations
    @Override
    public SpaceRateOverrideDTO createSpaceRateOverride(SpaceRateOverrideRegistrationDTO registrationDTO) {
        // Check if the assignment already exists
        if (spaceRateOverrideRepository.existsBySpaceIdAndRatePlanId(
                registrationDTO.parkingSpaceId(), registrationDTO.ratePlanId())) {
            throw new ResourceConflictException(
                "Space rate override already exists for space " + registrationDTO.parkingSpaceId() + 
                " and rate plan " + registrationDTO.ratePlanId());
        }
        
        SpaceRateOverride override = new SpaceRateOverride();
        return getSpaceRateOverrideDTO(registrationDTO, override);
    }

    private SpaceRateOverrideDTO getSpaceRateOverrideDTO(SpaceRateOverrideRegistrationDTO registrationDTO, SpaceRateOverride override) {
        override.setSpace(parkingSpaceRepository.findById(registrationDTO.parkingSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found with id: " + registrationDTO.parkingSpaceId())));
        override.setRatePlan(ratePlanRepository.findById(registrationDTO.ratePlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Rate plan not found with id: " + registrationDTO.ratePlanId())));
        override.setPriority(registrationDTO.priority() != null ? registrationDTO.priority() : 100);
        override.setEffectiveFrom(registrationDTO.effectiveFrom());
        override.setEffectiveTo(registrationDTO.effectiveTo());

        SpaceRateOverride saved = spaceRateOverrideRepository.save(override);
        return mapToSpaceRateOverrideDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceRateOverrideDTO getSpaceRateOverrideById(Long id) {
        SpaceRateOverride override = spaceRateOverrideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space rate override not found with id: " + id));
        return mapToSpaceRateOverrideDTO(override);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SpaceRateOverrideDTO> getSpaceRateOverridesBySpaceId(Long spaceId, Pageable pageable) {
        return spaceRateOverrideRepository.findBySpaceId(spaceId, pageable)
                .map(this::mapToSpaceRateOverrideDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SpaceRateOverrideDTO> getAllSpaceRateOverrides(Pageable pageable) {
        return spaceRateOverrideRepository.findAll(pageable)
                .map(this::mapToSpaceRateOverrideDTO);
    }
    
    @Override
    public SpaceRateOverrideDTO updateSpaceRateOverride(Long id, SpaceRateOverrideRegistrationDTO registrationDTO) {
        SpaceRateOverride override = spaceRateOverrideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space rate override not found with id: " + id));

        return getSpaceRateOverrideDTO(registrationDTO, override);
    }
    
    @Override
    public void deleteSpaceRateOverride(Long id) {
        if (!spaceRateOverrideRepository.existsById(id)) {
            throw new ResourceNotFoundException("Space rate override not found with id: " + id);
        }
        spaceRateOverrideRepository.deleteById(id);
    }
    
    // Pricing operations
    @Override
    @Transactional(readOnly = true)
    public Money getPricingQuote(PricingQuoteDTO quoteDTO) {
        return pricingService.quote(
                quoteDTO.parkingLotId(),
                quoteDTO.parkingSpaceId(),
                quoteDTO.vehicleType(),
                quoteDTO.userGroup(),
                quoteDTO.startTime(),
                quoteDTO.endTime()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public Money getStandaloneSpacePricingQuote(Long spaceId, VehicleType vehicleType, UserGroup userGroup,
                                                ZonedDateTime startTime, ZonedDateTime endTime) {
        return pricingService.quote(
                null, // No lot ID for standalone spaces
                spaceId,
                vehicleType,
                userGroup,
                startTime,
                endTime
        );
    }
    
    // Mapping methods
    private RatePlanDTO mapToRatePlanDTO(RatePlan ratePlan) {
        return new RatePlanDTO(
                ratePlan.getId(),
                ratePlan.getName(),
                ratePlan.getType(),
                ratePlan.getCurrency(),
                ratePlan.getTimeZone(),
                ratePlan.getGraceMinutes(),
                ratePlan.getIncrementMinutes(),
                ratePlan.getDailyCap(),
                ratePlan.getActive(),
                ratePlan.getCreatedAt(),
                ratePlan.getUpdatedAt()
        );
    }
    
    private RateRuleDTO mapToRateRuleDTO(RateRule rateRule) {
        return new RateRuleDTO(
                rateRule.getId(),
                rateRule.getRatePlan().getId(),
                rateRule.getStartMinute(),
                rateRule.getEndMinute(),
                rateRule.getStartTime(),
                rateRule.getEndTime(),
                rateRule.getDayOfWeek(),
                rateRule.getVehicleType(),
                rateRule.getUserGroup(),
                rateRule.getPricePerHour(),
                rateRule.getPriceFlat(),
                rateRule.getCreatedAt(),
                rateRule.getUpdatedAt()
        );
    }
    
    private LotRateAssignmentDTO mapToLotRateAssignmentDTO(LotRateAssignment assignment) {
        return new LotRateAssignmentDTO(
                assignment.getId(),
                assignment.getLot().getId(),
                assignment.getRatePlan().getId(),
                assignment.getPriority(),
                assignment.getEffectiveFrom(),
                assignment.getEffectiveTo(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt()
        );
    }
    
    private SpaceRateOverrideDTO mapToSpaceRateOverrideDTO(SpaceRateOverride override) {
        return new SpaceRateOverrideDTO(
                override.getId(),
                override.getSpace().getId(),
                override.getRatePlan().getId(),
                override.getPriority(),
                override.getEffectiveFrom(),
                override.getEffectiveTo(),
                override.getCreatedAt(),
                override.getUpdatedAt()
        );
    }
}
