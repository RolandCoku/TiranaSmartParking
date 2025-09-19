package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParkingLotService {
    // Existing methods
    ParkingLotResponseDTO registerParkingLot(ParkingLotRegistrationDTO parkingLotRegistrationDTO);
    ParkingLotResponseDTO getParkingLotById(Long id);
    Page<ParkingLotResponseDTO> getAllParkingLots(Pageable pageable);
    ParkingLotResponseDTO updateParkingLot(Long id, ParkingLotRegistrationDTO parkingLotRegistrationDTO);
    ParkingLotResponseDTO patchParkingLot(Long id, ParkingLotRegistrationDTO parkingLotRegistrationDTO);
    void deleteParkingLot(Long id);
    
    // New user-facing methods
    ParkingLotDetailDTO getParkingLotDetailById(Long id);
    Page<ParkingLotSearchDTO> getAllParkingLotsForUsers(Pageable pageable);
    Page<ParkingLotSearchDTO> findNearbyParkingLots(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    Page<ParkingLotSearchDTO> findAvailableParkingLots(Pageable pageable);
    Page<ParkingLotSearchDTO> findParkingLotsBySpaceType(ParkingSpace.SpaceType spaceType, Pageable pageable);
    Page<ParkingLotSearchDTO> findParkingLotsByFeatures(Boolean hasChargingStations, Boolean hasDisabledAccess, 
                                                       Boolean hasCctv, Boolean covered, Pageable pageable);
    Page<ParkingLotSearchDTO> searchParkingLots(String searchTerm, Pageable pageable);
    
    // Availability methods
    Integer getAvailableSpacesCount(Long lotId);
    Integer getAvailableSpacesCountByType(Long lotId, ParkingSpace.SpaceType spaceType);
    Double getAvailabilityPercentage(Long lotId);
    java.util.List<ParkingSpace.SpaceType> getAvailableSpaceTypes(Long lotId);
    
    // Real-time operations
    void updateParkingLotAvailability(Long lotId);
    
    // Reviews and Images
    Page<ReviewSummaryDTO> getParkingLotReviews(Long lotId, Pageable pageable);
    java.util.List<ParkingSpaceImageDTO> getParkingLotImages(Long lotId);
}
