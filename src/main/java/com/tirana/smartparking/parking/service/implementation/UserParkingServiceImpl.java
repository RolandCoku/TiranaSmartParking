package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.entity.Review;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.repository.ReviewRepository;
import com.tirana.smartparking.parking.service.UserParkingService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserParkingServiceImpl implements UserParkingService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserParkingServiceImpl.class);
    
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReviewRepository reviewRepository;
    private final GeometryFactory geometryFactory;
    
    public UserParkingServiceImpl(ParkingLotRepository parkingLotRepository,
                                 ParkingSpaceRepository parkingSpaceRepository,
                                 ReviewRepository reviewRepository,
                                 GeometryFactory geometryFactory) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.reviewRepository = reviewRepository;
        this.geometryFactory = geometryFactory;
    }
    
    @Override
    public ParkingLotDetailDTO getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + id));
        
        return mapToParkingLotDetailDTO(parkingLot);
    }
    
    @Override
    public Page<ParkingLotSearchDTO> getAllParkingLots(Pageable pageable) {
        return parkingLotRepository.findAll(pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    public Page<ParkingLotSearchDTO> findNearbyParkingLots(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        double radiusMeters = radiusKm * 1000; // Convert km to meters
        
        List<ParkingLot> nearbyLots = parkingLotRepository.findNearbyParkingLots(point, radiusMeters);
        
        // Convert to Page manually since we have a List
        List<ParkingLotSearchDTO> searchDTOs = nearbyLots.stream()
                .map(this::mapToParkingLotSearchDTO)
                .collect(Collectors.toList());
        
        // Calculate distances and sort
        searchDTOs.forEach(dto -> {
            double distance = calculateDistance(latitude, longitude, dto.latitude(), dto.longitude());
            // Note: We can't modify the DTO here since it's a record, so we'll handle distance in the controller
        });
        
        // For now, return all results as a single page
        // In a real implementation, you'd want to implement proper pagination
        return new org.springframework.data.domain.PageImpl<>(searchDTOs, pageable, searchDTOs.size());
    }
    
    @Override
    public Page<ParkingLotSearchDTO> findAvailableParkingLots(Pageable pageable) {
        return parkingLotRepository.findAvailableParkingLots(pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    public Page<ParkingLotSearchDTO> findParkingLotsBySpaceType(ParkingSpace.SpaceType spaceType, Pageable pageable) {
        return parkingLotRepository.findParkingLotsBySpaceType(spaceType, pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    public Page<ParkingLotSearchDTO> findParkingLotsByFeatures(Boolean hasChargingStations, Boolean hasDisabledAccess, 
                                                             Boolean hasCctv, Boolean covered, Pageable pageable) {
        return parkingLotRepository.findParkingLotsByFeatures(hasChargingStations, hasDisabledAccess, hasCctv, covered, pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    public Page<ParkingLotSearchDTO> searchParkingLots(String searchTerm, Pageable pageable) {
        return parkingLotRepository.searchParkingLots(searchTerm, pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    public ParkingSpaceDetailDTO getParkingSpaceById(Long id) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found with id: " + id));
        
        return mapToParkingSpaceDetailDTO(parkingSpace);
    }
    
    @Override
    public Page<ParkingSpaceSummaryDTO> getParkingSpacesByLotId(Long lotId, Pageable pageable) {
        List<ParkingSpace> spaces = parkingSpaceRepository.findByParkingLotId(lotId);
        List<ParkingSpaceSummaryDTO> summaryDTOs = spaces.stream()
                .map(this::mapToParkingSpaceSummaryDTO)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(summaryDTOs, pageable, summaryDTOs.size());
    }
    
    @Override
    public Page<ParkingSpaceSummaryDTO> getAvailableParkingSpacesByLotId(Long lotId, Pageable pageable) {
        List<ParkingSpace> spaces = parkingSpaceRepository.findAvailableSpacesByLotId(lotId);
        List<ParkingSpaceSummaryDTO> summaryDTOs = spaces.stream()
                .map(this::mapToParkingSpaceSummaryDTO)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(summaryDTOs, pageable, summaryDTOs.size());
    }
    
    @Override
    public Page<ParkingSpaceSummaryDTO> findNearbyAvailableSpaces(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        double radiusMeters = radiusKm * 1000;
        
        List<ParkingSpace> spaces = parkingSpaceRepository.findNearbyAvailableSpaces(point, radiusMeters);
        List<ParkingSpaceSummaryDTO> summaryDTOs = spaces.stream()
                .map(this::mapToParkingSpaceSummaryDTO)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(summaryDTOs, pageable, summaryDTOs.size());
    }
    
    @Override
    public Page<ParkingSpaceSummaryDTO> findParkingSpacesByType(ParkingSpace.SpaceType spaceType, Pageable pageable) {
        return parkingSpaceRepository.findBySpaceTypeAndStatus(spaceType, ParkingSpace.SpaceStatus.AVAILABLE, pageable)
                .map(this::mapToParkingSpaceSummaryDTO);
    }
    
    @Override
    public Page<ParkingSpaceSummaryDTO> findParkingSpacesByTypeAndStatus(ParkingSpace.SpaceType spaceType, 
                                                                        ParkingSpace.SpaceStatus status, Pageable pageable) {
        return parkingSpaceRepository.findBySpaceTypeAndStatus(spaceType, status, pageable)
                .map(this::mapToParkingSpaceSummaryDTO);
    }
    
    @Override
    public Integer getAvailableSpacesCount(Long lotId) {
        return parkingSpaceRepository.findAvailableSpacesByLotId(lotId).size();
    }
    
    @Override
    public Integer getAvailableSpacesCountByType(Long lotId, ParkingSpace.SpaceType spaceType) {
        return (int) parkingSpaceRepository.countAvailableSpacesByLotIdAndSpaceType(lotId, spaceType);
    }
    
    @Override
    public Double getAvailabilityPercentage(Long lotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + lotId));
        
        if (parkingLot.getCapacity() == null || parkingLot.getCapacity() == 0) {
            return 0.0;
        }
        
        Integer availableSpaces = getAvailableSpacesCount(lotId);
        return (double) availableSpaces / parkingLot.getCapacity() * 100;
    }
    
    @Override
    public List<ParkingSpace.SpaceType> getAvailableSpaceTypes(Long lotId) {
        return parkingSpaceRepository.findAvailableSpacesByLotId(lotId).stream()
                .map(ParkingSpace::getSpaceType)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateParkingLotAvailability(Long lotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + lotId));
        
        Integer availableSpaces = getAvailableSpacesCount(lotId);
        parkingLot.setAvailableSpaces(availableSpaces);
        parkingLot.setAvailabilityUpdatedAt(Instant.now());
        
        parkingLotRepository.save(parkingLot);
        
        logger.info("Updated availability for parking lot {}: {} available spaces", lotId, availableSpaces);
    }
    
    @Override
    @Transactional
    public void updateParkingSpaceStatus(Long spaceId, ParkingSpace.SpaceStatus status) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found with id: " + spaceId));
        
        parkingSpace.setSpaceStatus(status);
        parkingSpace.setLastStatusChangedAt(Instant.now());
        
        parkingSpaceRepository.save(parkingSpace);
        
        // Update the parent parking lot's availability
        if (parkingSpace.getParkingLot() != null) {
            updateParkingLotAvailability(parkingSpace.getParkingLot().getId());
        }
        
        logger.info("Updated status for parking space {}: {}", spaceId, status);
    }
    
    // Mapping methods
    private ParkingLotDetailDTO mapToParkingLotDetailDTO(ParkingLot parkingLot) {
        Point location = parkingLot.getLocation();
        Double latitude = location != null ? location.getY() : null;
        Double longitude = location != null ? location.getX() : null;
        
        List<ParkingSpaceSummaryDTO> spaces = parkingLot.getParkingSpaces().stream()
                .map(this::mapToParkingSpaceSummaryDTO)
                .collect(Collectors.toList());
        
        List<ReviewSummaryDTO> reviews = parkingLot.getReviews().stream()
                .map(this::mapToReviewSummaryDTO)
                .collect(Collectors.toList());
        
        Double averageRating = calculateAverageRating(parkingLot.getReviews());
        Integer totalReviews = parkingLot.getReviews().size();
        
        Integer occupiedSpaces = parkingLot.getCapacity() != null && parkingLot.getAvailableSpaces() != null 
                ? parkingLot.getCapacity() - parkingLot.getAvailableSpaces() 
                : 0;
        
        Double availabilityPercentage = parkingLot.getCapacity() != null && parkingLot.getCapacity() > 0
                ? (double) parkingLot.getAvailableSpaces() / parkingLot.getCapacity() * 100
                : 0.0;
        
        return new ParkingLotDetailDTO(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getDescription(),
                parkingLot.getAddress(),
                parkingLot.getPhone(),
                parkingLot.getEmail(),
                parkingLot.getOperatingHours(),
                parkingLot.getStatus(),
                latitude,
                longitude,
                parkingLot.getPublicAccess(),
                parkingLot.getHasChargingStations(),
                parkingLot.getHasDisabledAccess(),
                parkingLot.getHasCctv(),
                parkingLot.getCovered(),
                parkingLot.getCapacity(),
                parkingLot.getAvailableSpaces(),
                occupiedSpaces,
                availabilityPercentage,
                parkingLot.getAvailabilityUpdatedAt(),
                spaces,
                reviews,
                averageRating,
                totalReviews,
                parkingLot.getCreatedAt(),
                parkingLot.getUpdatedAt()
        );
    }
    
    private ParkingLotSearchDTO mapToParkingLotSearchDTO(ParkingLot parkingLot) {
        Point location = parkingLot.getLocation();
        Double latitude = location != null ? location.getY() : null;
        Double longitude = location != null ? location.getX() : null;
        
        Double availabilityPercentage = parkingLot.getCapacity() != null && parkingLot.getCapacity() > 0
                ? (double) parkingLot.getAvailableSpaces() / parkingLot.getCapacity() * 100
                : 0.0;
        
        Double averageRating = calculateAverageRating(parkingLot.getReviews());
        Integer totalReviews = parkingLot.getReviews().size();
        
        List<ParkingSpace.SpaceType> availableSpaceTypes = getAvailableSpaceTypes(parkingLot.getId());
        
        return new ParkingLotSearchDTO(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getDescription(),
                parkingLot.getAddress(),
                parkingLot.getOperatingHours(),
                parkingLot.getStatus(),
                latitude,
                longitude,
                null, // Distance will be calculated in controller
                parkingLot.getPublicAccess(),
                parkingLot.getHasChargingStations(),
                parkingLot.getHasDisabledAccess(),
                parkingLot.getHasCctv(),
                parkingLot.getCovered(),
                parkingLot.getCapacity(),
                parkingLot.getAvailableSpaces(),
                availabilityPercentage,
                parkingLot.getAvailabilityUpdatedAt(),
                averageRating,
                totalReviews,
                availableSpaceTypes
        );
    }
    
    private ParkingSpaceSummaryDTO mapToParkingSpaceSummaryDTO(ParkingSpace parkingSpace) {
        Point location = parkingSpace.getLocation();
        Double latitude = location != null ? location.getY() : null;
        Double longitude = location != null ? location.getX() : null;
        
        return new ParkingSpaceSummaryDTO(
                parkingSpace.getId(),
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getId() : null,
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getName() : null,
                latitude,
                longitude,
                parkingSpace.getSpaceType(),
                parkingSpace.getSpaceStatus(),
                parkingSpace.getLabel(),
                parkingSpace.getDescription(),
                parkingSpace.getSensorDevice() != null,
                parkingSpace.getSensorDevice() != null ? parkingSpace.getSensorDevice().getId() : null,
                parkingSpace.getLastStatusChangedAt(),
                parkingSpace.getCreatedAt(),
                parkingSpace.getUpdatedAt()
        );
    }
    
    private ParkingSpaceDetailDTO mapToParkingSpaceDetailDTO(ParkingSpace parkingSpace) {
        Point location = parkingSpace.getLocation();
        Double latitude = location != null ? location.getY() : null;
        Double longitude = location != null ? location.getX() : null;
        
        List<ReviewSummaryDTO> reviews = parkingSpace.getReviews().stream()
                .map(this::mapToReviewSummaryDTO)
                .collect(Collectors.toList());
        
        Double averageRating = calculateAverageRating(parkingSpace.getReviews());
        Integer totalReviews = parkingSpace.getReviews().size();
        
        return new ParkingSpaceDetailDTO(
                parkingSpace.getId(),
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getId() : null,
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getName() : null,
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getAddress() : null,
                latitude,
                longitude,
                parkingSpace.getSpaceType(),
                parkingSpace.getSpaceStatus(),
                parkingSpace.getLabel(),
                parkingSpace.getDescription(),
                parkingSpace.getSensorDevice() != null,
                parkingSpace.getSensorDevice() != null ? parkingSpace.getSensorDevice().getId() : null,
                parkingSpace.getSensorDevice() != null ? parkingSpace.getSensorDevice().getStatus().name() : null,
                parkingSpace.getLastStatusChangedAt(),
                List.of(), // Images - would need to implement
                reviews,
                averageRating,
                totalReviews,
                parkingSpace.getCreatedAt(),
                parkingSpace.getUpdatedAt()
        );
    }
    
    private ReviewSummaryDTO mapToReviewSummaryDTO(Review review) {
        return new ReviewSummaryDTO(
                review.getId(),
                review.getUser() != null ? review.getUser().getId() : null,
                review.getUser() != null ? review.getUser().getEmail() : null,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
    
    private Double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
}
