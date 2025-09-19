package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.service.ParkingLotService;
import com.tirana.smartparking.parking.service.ParkingSpaceService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking")
@Validated
public class ParkingController {

    private final ParkingLotService parkingLotService;
    private final ParkingSpaceService parkingSpaceService;

    public ParkingController(ParkingLotService parkingLotService, ParkingSpaceService parkingSpaceService) {
        this.parkingLotService = parkingLotService;
        this.parkingSpaceService = parkingSpaceService;
    }

    // ==================== PARKING LOTS ====================

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotSearchDTO>>> getAllParkingLots(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ParkingLotSearchDTO> parkingLots = parkingLotService.getAllParkingLotsForUsers(pageable);
        
        return ResponseHelper.ok("Parking lots retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingLots));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/{id}")
    public ResponseEntity<ApiResponse<ParkingLotDetailDTO>> getParkingLotById(@PathVariable Long id) {
        ParkingLotDetailDTO parkingLot = parkingLotService.getParkingLotDetailById(id);
        return ResponseHelper.ok("Parking lot retrieved successfully", parkingLot);
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/nearby")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotSearchDTO>>> findNearbyParkingLots(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam(defaultValue = "5.0") @DecimalMin("0.1") @DecimalMax("50.0") Double radiusKm,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingLotSearchDTO> parkingLots = parkingLotService.findNearbyParkingLots(latitude, longitude, radiusKm, pageable);
        
        return ResponseHelper.ok("Nearby parking lots retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingLots));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/available")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotSearchDTO>>> findAvailableParkingLots(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingLotSearchDTO> parkingLots = parkingLotService.findAvailableParkingLots(pageable);
        
        return ResponseHelper.ok("Available parking lots retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingLots));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotSearchDTO>>> searchParkingLots(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingLotSearchDTO> parkingLots = parkingLotService.searchParkingLots(query, pageable);
        
        return ResponseHelper.ok("Search results retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingLots));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/filter")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotSearchDTO>>> filterParkingLots(
            @RequestParam(required = false) Boolean hasChargingStations,
            @RequestParam(required = false) Boolean hasDisabledAccess,
            @RequestParam(required = false) Boolean hasCctv,
            @RequestParam(required = false) Boolean covered,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingLotSearchDTO> parkingLots = parkingLotService.findParkingLotsByFeatures(
                hasChargingStations, hasDisabledAccess, hasCctv, covered, pageable);
        
        return ResponseHelper.ok("Filtered parking lots retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingLots));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/by-space-type/{spaceType}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotSearchDTO>>> findParkingLotsBySpaceType(
            @PathVariable ParkingSpace.SpaceType spaceType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingLotSearchDTO> parkingLots = parkingLotService.findParkingLotsBySpaceType(spaceType, pageable);
        
        return ResponseHelper.ok("Parking lots with " + spaceType + " spaces retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingLots));
    }

    // ==================== PARKING SPACES ====================

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/spaces/{id}")
    public ResponseEntity<ApiResponse<ParkingSpaceDetailDTO>> getParkingSpaceById(@PathVariable Long id) {
        ParkingSpaceDetailDTO parkingSpace = parkingSpaceService.getParkingSpaceDetailById(id);
        return ResponseHelper.ok("Parking space retrieved successfully", parkingSpace);
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/spaces/lot/{lotId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSpaceSummaryDTO>>> getParkingSpacesByLotId(
            @PathVariable Long lotId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingSpaceSummaryDTO> parkingSpaces = parkingSpaceService.getParkingSpacesByLotId(lotId, pageable);
        
        return ResponseHelper.ok("Parking spaces retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingSpaces));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/spaces/lot/{lotId}/available")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSpaceSummaryDTO>>> getAvailableParkingSpacesByLotId(
            @PathVariable Long lotId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingSpaceSummaryDTO> parkingSpaces = parkingSpaceService.getAvailableParkingSpacesByLotId(lotId, pageable);
        
        return ResponseHelper.ok("Available parking spaces retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingSpaces));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/spaces/nearby")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSpaceSummaryDTO>>> findNearbyAvailableSpaces(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam(defaultValue = "2.0") @DecimalMin("0.1") @DecimalMax("20.0") Double radiusKm,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingSpaceSummaryDTO> parkingSpaces = parkingSpaceService.findNearbyAvailableSpaces(
                latitude, longitude, radiusKm, pageable);
        
        return ResponseHelper.ok("Nearby available parking spaces retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingSpaces));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/spaces/by-type/{spaceType}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSpaceSummaryDTO>>> findParkingSpacesByType(
            @PathVariable ParkingSpace.SpaceType spaceType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingSpaceSummaryDTO> parkingSpaces = parkingSpaceService.findParkingSpacesByType(spaceType, pageable);
        
        return ResponseHelper.ok("Available " + spaceType + " parking spaces retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingSpaces));
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/spaces/by-type-and-status/{spaceType}/{status}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSpaceSummaryDTO>>> findParkingSpacesByTypeAndStatus(
            @PathVariable ParkingSpace.SpaceType spaceType,
            @PathVariable ParkingSpace.SpaceStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ParkingSpaceSummaryDTO> parkingSpaces = parkingSpaceService.findParkingSpacesByTypeAndStatus(
                spaceType, status, pageable);
        
        return ResponseHelper.ok(spaceType + " parking spaces with " + status + " status retrieved successfully", 
                PaginationUtil.toPaginatedResponse(parkingSpaces));
    }
    
    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/availability/lot/{lotId}")
    public ResponseEntity<ApiResponse<AvailabilityInfoDTO>> getParkingLotAvailability(@PathVariable Long lotId) {
        Integer availableSpaces = parkingLotService.getAvailableSpacesCount(lotId);
        Double availabilityPercentage = parkingLotService.getAvailabilityPercentage(lotId);
        var availableSpaceTypes = parkingLotService.getAvailableSpaceTypes(lotId);
        
        AvailabilityInfoDTO availabilityInfo = new AvailabilityInfoDTO(
                lotId, availableSpaces, availabilityPercentage, availableSpaceTypes);
        
        return ResponseHelper.ok("Availability information retrieved successfully", availabilityInfo);
    }

    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/availability/lot/{lotId}/by-type/{spaceType}")
    public ResponseEntity<ApiResponse<Integer>> getAvailableSpacesCountByType(
            @PathVariable Long lotId,
            @PathVariable ParkingSpace.SpaceType spaceType) {
        
        Integer count = parkingLotService.getAvailableSpacesCountByType(lotId, spaceType);
        return ResponseHelper.ok("Available " + spaceType + " spaces count retrieved successfully", count);
    }

    @PreAuthorize("hasAuthority('PARKING_UPDATE')")
    @PostMapping("/availability/lot/{lotId}/update")
    public ResponseEntity<ApiResponse<Void>> updateParkingLotAvailability(@PathVariable Long lotId) {
        parkingLotService.updateParkingLotAvailability(lotId);
        return ResponseHelper.ok("Parking lot availability updated successfully", null);
    }

    @PreAuthorize("hasAuthority('PARKING_UPDATE')")
    @PostMapping("/availability/space/{spaceId}/update-status")
    public ResponseEntity<ApiResponse<Void>> updateParkingSpaceStatus(
            @PathVariable Long spaceId,
            @RequestParam ParkingSpace.SpaceStatus status) {
        
        parkingSpaceService.updateParkingSpaceStatus(spaceId, status);
        return ResponseHelper.ok("Parking space status updated successfully", null);
    }


    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/{lotId}/reviews")
    public ResponseEntity<ApiResponse<PaginatedResponse<ReviewSummaryDTO>>> getParkingLotReviews(
            @PathVariable Long lotId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReviewSummaryDTO> reviews = parkingLotService.getParkingLotReviews(lotId, pageable);
        
        return ResponseHelper.ok("Parking lot reviews retrieved successfully", 
                PaginationUtil.toPaginatedResponse(reviews));
    }


    @PreAuthorize("hasAuthority('PARKING_READ')")
    @GetMapping("/lots/{lotId}/images")
    public ResponseEntity<ApiResponse<List<ParkingSpaceImageDTO>>> getParkingLotImages(@PathVariable Long lotId) {
        List<ParkingSpaceImageDTO> images = parkingLotService.getParkingLotImages(lotId);
        return ResponseHelper.ok("Parking lot images retrieved successfully", images);
    }
}

