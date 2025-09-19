package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.common.util.SortParser;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.service.RateManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/rates")
public class RateManagementController {
    
    private final RateManagementService rateManagementService;
    private final SortParser sortParser;
    
    public RateManagementController(RateManagementService rateManagementService, SortParser sortParser) {
        this.rateManagementService = rateManagementService;
        this.sortParser = sortParser;
    }
    
    // Rate Plan endpoints
    @PreAuthorize("hasAuthority('RATE_CREATE')")
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<RatePlanDTO>> createRatePlan(@Validated @RequestBody RatePlanRegistrationDTO registrationDTO) {
        return ResponseHelper.created("Rate plan created successfully", rateManagementService.createRatePlan(registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<RatePlanDTO>> getRatePlanById(@PathVariable Long id) {
        return ResponseHelper.ok("Rate plan fetched successfully", rateManagementService.getRatePlanById(id));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<PaginatedResponse<RatePlanDTO>>> getAllRatePlans(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RatePlanDTO> plansPage = rateManagementService.getAllRatePlans(pageable);
        
        PaginatedResponse<RatePlanDTO> response = PaginationUtil.toPaginatedResponse(plansPage);
        return ResponseHelper.ok("Rate plans fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_UPDATE')")
    @PutMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<RatePlanDTO>> updateRatePlan(@PathVariable Long id, @Validated @RequestBody RatePlanRegistrationDTO registrationDTO) {
        return ResponseHelper.ok("Rate plan updated successfully", rateManagementService.updateRatePlan(id, registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_DELETE')")
    @DeleteMapping("/plans/{id}")
    public ResponseEntity<?> deleteRatePlan(@PathVariable Long id) {
        rateManagementService.deleteRatePlan(id);
        return ResponseHelper.noContent();
    }
    
    // Rate Rule endpoints
    @PreAuthorize("hasAuthority('RATE_CREATE')")
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<RateRuleDTO>> createRateRule(@Validated @RequestBody RateRuleRegistrationDTO registrationDTO) {
        return ResponseHelper.created("Rate rule created successfully", rateManagementService.createRateRule(registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<RateRuleDTO>> getRateRuleById(@PathVariable Long id) {
        return ResponseHelper.ok("Rate rule fetched successfully", rateManagementService.getRateRuleById(id));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<PaginatedResponse<RateRuleDTO>>> getAllRateRules(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RateRuleDTO> rulesPage = rateManagementService.getAllRateRules(pageable);
        
        PaginatedResponse<RateRuleDTO> response = PaginationUtil.toPaginatedResponse(rulesPage);
        return ResponseHelper.ok("Rate rules fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/plans/{planId}/rules")
    public ResponseEntity<ApiResponse<PaginatedResponse<RateRuleDTO>>> getRateRulesByPlanId(
            @PathVariable Long planId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RateRuleDTO> rulesPage = rateManagementService.getRateRulesByPlanId(planId, pageable);
        
        PaginatedResponse<RateRuleDTO> response = PaginationUtil.toPaginatedResponse(rulesPage);
        return ResponseHelper.ok("Rate rules for plan fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_UPDATE')")
    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<RateRuleDTO>> updateRateRule(@PathVariable Long id, @Validated @RequestBody RateRuleRegistrationDTO registrationDTO) {
        return ResponseHelper.ok("Rate rule updated successfully", rateManagementService.updateRateRule(id, registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_DELETE')")
    @DeleteMapping("/rules/{id}")
    public ResponseEntity<?> deleteRateRule(@PathVariable Long id) {
        rateManagementService.deleteRateRule(id);
        return ResponseHelper.noContent();
    }
    
    // Lot Rate Assignment endpoints
    @PreAuthorize("hasAuthority('RATE_CREATE')")
    @PostMapping("/lot-assignments")
    public ResponseEntity<ApiResponse<LotRateAssignmentDTO>> createLotRateAssignment(@Validated @RequestBody LotRateAssignmentRegistrationDTO registrationDTO) {
        return ResponseHelper.created("Lot rate assignment created successfully", rateManagementService.createLotRateAssignment(registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/lot-assignments/{id}")
    public ResponseEntity<ApiResponse<LotRateAssignmentDTO>> getLotRateAssignmentById(@PathVariable Long id) {
        return ResponseHelper.ok("Lot rate assignment fetched successfully", rateManagementService.getLotRateAssignmentById(id));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/lot-assignments")
    public ResponseEntity<ApiResponse<PaginatedResponse<LotRateAssignmentDTO>>> getAllLotRateAssignments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LotRateAssignmentDTO> assignmentsPage = rateManagementService.getAllLotRateAssignments(pageable);
        
        PaginatedResponse<LotRateAssignmentDTO> response = PaginationUtil.toPaginatedResponse(assignmentsPage);
        return ResponseHelper.ok("Lot rate assignments fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/lots/{lotId}/rate-assignments")
    public ResponseEntity<ApiResponse<PaginatedResponse<LotRateAssignmentDTO>>> getLotRateAssignmentsByLotId(
            @PathVariable Long lotId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LotRateAssignmentDTO> assignmentsPage = rateManagementService.getLotRateAssignmentsByLotId(lotId, pageable);
        
        PaginatedResponse<LotRateAssignmentDTO> response = PaginationUtil.toPaginatedResponse(assignmentsPage);
        return ResponseHelper.ok("Lot rate assignments fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_UPDATE')")
    @PutMapping("/lot-assignments/{id}")
    public ResponseEntity<ApiResponse<LotRateAssignmentDTO>> updateLotRateAssignment(@PathVariable Long id, @Validated @RequestBody LotRateAssignmentRegistrationDTO registrationDTO) {
        return ResponseHelper.ok("Lot rate assignment updated successfully", rateManagementService.updateLotRateAssignment(id, registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_DELETE')")
    @DeleteMapping("/lot-assignments/{id}")
    public ResponseEntity<?> deleteLotRateAssignment(@PathVariable Long id) {
        rateManagementService.deleteLotRateAssignment(id);
        return ResponseHelper.noContent();
    }
    
    // Space Rate Override endpoints
    @PreAuthorize("hasAuthority('RATE_CREATE')")
    @PostMapping("/space-overrides")
    public ResponseEntity<ApiResponse<SpaceRateOverrideDTO>> createSpaceRateOverride(@Validated @RequestBody SpaceRateOverrideRegistrationDTO registrationDTO) {
        return ResponseHelper.created("Space rate override created successfully", rateManagementService.createSpaceRateOverride(registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/space-overrides/{id}")
    public ResponseEntity<ApiResponse<SpaceRateOverrideDTO>> getSpaceRateOverrideById(@PathVariable Long id) {
        return ResponseHelper.ok("Space rate override fetched successfully", rateManagementService.getSpaceRateOverrideById(id));
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/space-overrides")
    public ResponseEntity<ApiResponse<PaginatedResponse<SpaceRateOverrideDTO>>> getAllSpaceRateOverrides(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SpaceRateOverrideDTO> overridesPage = rateManagementService.getAllSpaceRateOverrides(pageable);
        
        PaginatedResponse<SpaceRateOverrideDTO> response = PaginationUtil.toPaginatedResponse(overridesPage);
        return ResponseHelper.ok("Space rate overrides fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_READ')")
    @GetMapping("/spaces/{spaceId}/rate-overrides")
    public ResponseEntity<ApiResponse<PaginatedResponse<SpaceRateOverrideDTO>>> getSpaceRateOverridesBySpaceId(
            @PathVariable Long spaceId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy) {
        
        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SpaceRateOverrideDTO> overridesPage = rateManagementService.getSpaceRateOverridesBySpaceId(spaceId, pageable);
        
        PaginatedResponse<SpaceRateOverrideDTO> response = PaginationUtil.toPaginatedResponse(overridesPage);
        return ResponseHelper.ok("Space rate overrides fetched successfully", response);
    }
    
    @PreAuthorize("hasAuthority('RATE_UPDATE')")
    @PutMapping("/space-overrides/{id}")
    public ResponseEntity<ApiResponse<SpaceRateOverrideDTO>> updateSpaceRateOverride(@PathVariable Long id, @Validated @RequestBody SpaceRateOverrideRegistrationDTO registrationDTO) {
        return ResponseHelper.ok("Space rate override updated successfully", rateManagementService.updateSpaceRateOverride(id, registrationDTO));
    }
    
    @PreAuthorize("hasAuthority('RATE_DELETE')")
    @DeleteMapping("/space-overrides/{id}")
    public ResponseEntity<?> deleteSpaceRateOverride(@PathVariable Long id) {
        rateManagementService.deleteSpaceRateOverride(id);
        return ResponseHelper.noContent();
    }
}
