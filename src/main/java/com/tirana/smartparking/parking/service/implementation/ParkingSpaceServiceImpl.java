package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.entity.Review;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.repository.ReviewRepository;
import com.tirana.smartparking.parking.sensor.repository.SensorDeviceRepository;
import com.tirana.smartparking.parking.service.ParkingSpaceService;
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
@Transactional
public class ParkingSpaceServiceImpl implements ParkingSpaceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ParkingSpaceServiceImpl.class);
    
    private final ParkingLotRepository parkingLotRepository;
    private final GeometryFactory geometryFactory;
    private final SensorDeviceRepository sensorRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReviewRepository reviewRepository;

    public ParkingSpaceServiceImpl(ParkingLotRepository parkingLotRepository, 
                                  GeometryFactory geometryFactory, 
                                  SensorDeviceRepository sensorRepository, 
                                  ParkingSpaceRepository parkingSpaceRepository,
                                  ReviewRepository reviewRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.geometryFactory = geometryFactory;
        this.sensorRepository = sensorRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Page<ParkingSpaceResponseDTO> getAllParkingSpaces(Pageable pageable) {
        return parkingSpaceRepository.findAll(pageable).map(this::mapToParkingSpaceResponseDTO);
    }

    @Override
    public ParkingSpaceResponseDTO getParkingSpaceById(Long id) {
        return parkingSpaceRepository.findById(id)
                .map(this::mapToParkingSpaceResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Parking space not found"));
    }

    @Override
    public ParkingSpaceResponseDTO registerParkingSpace(ParkingSpaceRegistrationDTO parkingSpaceRegistrationDTO) {
        ParkingSpace parkingSpace = new ParkingSpace();

        if (parkingSpaceRegistrationDTO.parkingLotId() != null) {
            parkingSpace.setParkingLot(parkingLotRepository.findById(parkingSpaceRegistrationDTO.parkingLotId())
                    .orElseThrow(() -> new IllegalArgumentException("Parking lot not found")));
        } else if (parkingSpaceRegistrationDTO.latitude() != null && parkingSpaceRegistrationDTO.longitude() != null) {
            parkingSpace.setLocation(geometryFactory.createPoint(new Coordinate(parkingSpaceRegistrationDTO.longitude(), parkingSpaceRegistrationDTO.latitude())));
        } else {
            throw new IllegalArgumentException("Either parking lot ID or coordinates must be provided");
        }

        parkingSpace.setSpaceType(ParkingSpace.SpaceType.valueOf(parkingSpaceRegistrationDTO.spaceType().toUpperCase()));
        parkingSpace.setSpaceStatus(ParkingSpace.SpaceStatus.valueOf(parkingSpaceRegistrationDTO.spaceStatus().toUpperCase()));
        parkingSpace.setLabel(parkingSpaceRegistrationDTO.label());
        parkingSpace.setDescription(parkingSpaceRegistrationDTO.description());

        if (parkingSpaceRegistrationDTO.sensorId() != null) {
            parkingSpace.setSensorDevice(sensorRepository.findById(parkingSpaceRegistrationDTO.sensorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found")));
        }

        return mapToParkingSpaceResponseDTO(parkingSpaceRepository.save(parkingSpace));
    }
    
    // ==================== NEW USER-FACING METHODS ====================
    
    @Override
    @Transactional(readOnly = true)
    public ParkingSpaceDetailDTO getParkingSpaceDetailById(Long id) {
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found with id: " + id));
        
        return mapToParkingSpaceDetailDTO(parkingSpace);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingSpaceSummaryDTO> getParkingSpacesByLotId(Long lotId, Pageable pageable) {
        List<ParkingSpace> spaces = parkingSpaceRepository.findByParkingLotId(lotId);
        List<ParkingSpaceSummaryDTO> summaryDTOs = spaces.stream()
                .map(this::mapToParkingSpaceSummaryDTO)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(summaryDTOs, pageable, summaryDTOs.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingSpaceSummaryDTO> getAvailableParkingSpacesByLotId(Long lotId, Pageable pageable) {
        List<ParkingSpace> spaces = parkingSpaceRepository.findAvailableSpacesByLotId(lotId);
        List<ParkingSpaceSummaryDTO> summaryDTOs = spaces.stream()
                .map(this::mapToParkingSpaceSummaryDTO)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(summaryDTOs, pageable, summaryDTOs.size());
    }
    
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<ParkingSpaceSummaryDTO> findParkingSpacesByType(ParkingSpace.SpaceType spaceType, Pageable pageable) {
        return parkingSpaceRepository.findBySpaceTypeAndStatus(spaceType, ParkingSpace.SpaceStatus.AVAILABLE, pageable)
                .map(this::mapToParkingSpaceSummaryDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingSpaceSummaryDTO> findParkingSpacesByTypeAndStatus(ParkingSpace.SpaceType spaceType, 
                                                                        ParkingSpace.SpaceStatus status, Pageable pageable) {
        return parkingSpaceRepository.findBySpaceTypeAndStatus(spaceType, status, pageable)
                .map(this::mapToParkingSpaceSummaryDTO);
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
            // This would need to be injected or called differently in a real implementation
            // For now, we'll just log it
            logger.info("Parking space {} status updated to {}, should update lot {} availability", 
                       spaceId, status, parkingSpace.getParkingLot().getId());
        }
        
        logger.info("Updated status for parking space {}: {}", spaceId, status);
    }
    
    // ==================== MAPPING METHODS ====================
    
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

    //TODO: This needs to be reviewed
    private ParkingSpaceResponseDTO mapToParkingSpaceResponseDTO(ParkingSpace parkingSpace) {
        return new ParkingSpaceResponseDTO(
                parkingSpace.getId(),
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getId() : null,
                parkingSpace.getLocation() != null ? parkingSpace.getLocation().getX() : null,
                parkingSpace.getLocation() != null ? parkingSpace.getLocation().getY() : null,
                parkingSpace.getSpaceType().name(),
                parkingSpace.getSpaceStatus().name(),
                parkingSpace.getLabel(),
                parkingSpace.getDescription(),
                parkingSpace.getSensorDevice() != null ? parkingSpace.getSensorDevice().getId() : null,
                parkingSpace.getCreatedAt(),
                parkingSpace.getUpdatedAt()
        );
    }
}

