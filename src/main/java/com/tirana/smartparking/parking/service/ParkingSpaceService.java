package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ParkingSpaceService {
    // Existing methods
    ParkingSpaceResponseDTO registerParkingSpace(ParkingSpaceRegistrationDTO parkingSpaceRegistrationDTO);
    Page<ParkingSpaceResponseDTO> getAllParkingSpaces(Pageable pageable);
    ParkingSpaceResponseDTO getParkingSpaceById(Long id);
    
    // New user-facing methods
    ParkingSpaceDetailDTO getParkingSpaceDetailById(Long id);
    Page<ParkingSpaceSummaryDTO> getParkingSpacesByLotId(Long lotId, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> getAvailableParkingSpacesByLotId(Long lotId, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> findNearbyAvailableSpaces(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> findParkingSpacesByType(ParkingSpace.SpaceType spaceType, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> findParkingSpacesByTypeAndStatus(ParkingSpace.SpaceType spaceType, 
                                                                ParkingSpace.SpaceStatus status, Pageable pageable);
    
    // Real-time operations
    void updateParkingSpaceStatus(Long spaceId, ParkingSpace.SpaceStatus status);
}
