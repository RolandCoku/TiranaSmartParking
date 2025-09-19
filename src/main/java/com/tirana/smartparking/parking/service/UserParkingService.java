package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserParkingService {
    
    // Parking Lot Operations
    ParkingLotDetailDTO getParkingLotById(Long id);
    Page<ParkingLotSearchDTO> getAllParkingLots(Pageable pageable);
    Page<ParkingLotSearchDTO> findNearbyParkingLots(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    Page<ParkingLotSearchDTO> findAvailableParkingLots(Pageable pageable);
    Page<ParkingLotSearchDTO> findParkingLotsBySpaceType(ParkingSpace.SpaceType spaceType, Pageable pageable);
    Page<ParkingLotSearchDTO> findParkingLotsByFeatures(Boolean hasChargingStations, Boolean hasDisabledAccess, 
                                                       Boolean hasCctv, Boolean covered, Pageable pageable);
    Page<ParkingLotSearchDTO> searchParkingLots(String searchTerm, Pageable pageable);
    
    // Parking Space Operations
    ParkingSpaceDetailDTO getParkingSpaceById(Long id);
    Page<ParkingSpaceSummaryDTO> getParkingSpacesByLotId(Long lotId, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> getAvailableParkingSpacesByLotId(Long lotId, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> findNearbyAvailableSpaces(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> findParkingSpacesByType(ParkingSpace.SpaceType spaceType, Pageable pageable);
    Page<ParkingSpaceSummaryDTO> findParkingSpacesByTypeAndStatus(ParkingSpace.SpaceType spaceType, 
                                                                ParkingSpace.SpaceStatus status, Pageable pageable);
    
    // Availability Operations
    Integer getAvailableSpacesCount(Long lotId);
    Integer getAvailableSpacesCountByType(Long lotId, ParkingSpace.SpaceType spaceType);
    Double getAvailabilityPercentage(Long lotId);
    List<ParkingSpace.SpaceType> getAvailableSpaceTypes(Long lotId);
    
    // Real-time Operations
    void updateParkingLotAvailability(Long lotId);
    void updateParkingSpaceStatus(Long spaceId, ParkingSpace.SpaceStatus status);
}
