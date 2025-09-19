package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.entity.ParkingSpaceImage;
import com.tirana.smartparking.parking.entity.Review;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.repository.ReviewRepository;
import com.tirana.smartparking.parking.service.ParkingLotService;
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
public class ParkingLotServiceImpl implements ParkingLotService {
    
    private static final Logger logger = LoggerFactory.getLogger(ParkingLotServiceImpl.class);
    
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReviewRepository reviewRepository;
    private final GeometryFactory geometryFactory;

    public ParkingLotServiceImpl(ParkingLotRepository parkingLotRepository, 
                                ParkingSpaceRepository parkingSpaceRepository,
                                ReviewRepository reviewRepository,
                                GeometryFactory geometryFactory) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.reviewRepository = reviewRepository;
        this.geometryFactory = geometryFactory;
    }

    @Override
    public Page<ParkingLotResponseDTO> getAllParkingLots(Pageable pageable) {
        return parkingLotRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public ParkingLotResponseDTO registerParkingLot(ParkingLotRegistrationDTO parkingLotRegistrationDTO) {

        ParkingLot parkingLot = new ParkingLot();

        if (parkingLotRegistrationDTO.source() == ParkingLot.Source.OSM) {
            if (parkingLotRegistrationDTO.osmId() == null || parkingLotRegistrationDTO.osmType() == null) {
                throw new IllegalArgumentException("OSM ID and OSM type must be provided for OSM source");
            }
            if (parkingLotRepository.findByOsmIdAndOsmType(parkingLotRegistrationDTO.osmId(), parkingLotRegistrationDTO.osmType()).isPresent()) {
                throw new IllegalArgumentException("Parking lot with this OSM ID and type already exists");
            }

            parkingLot.setSource(ParkingLot.Source.OSM);
            parkingLot.setOsmId(parkingLotRegistrationDTO.osmId());
            parkingLot.setOsmType(parkingLotRegistrationDTO.osmType());

        } else if (parkingLotRegistrationDTO.source() == ParkingLot.Source.MANUAL) {
            parkingLot.setSource(ParkingLot.Source.MANUAL);
        } else {
            throw new IllegalArgumentException("Invalid source type");
        }

        parkingLot.setName(parkingLotRegistrationDTO.name());
        parkingLot.setAddress(parkingLotRegistrationDTO.address());
        parkingLot.setDescription(parkingLotRegistrationDTO.description());
        parkingLot.setPublicAccess(parkingLotRegistrationDTO.publicAccess());
        parkingLot.setHasChargingStations(parkingLotRegistrationDTO.hasChargingStations());
        parkingLot.setHasDisabledAccess(parkingLotRegistrationDTO.hasDisabledAccess());
        parkingLot.setHasCctv(parkingLotRegistrationDTO.hasCctv());

        parkingLot.setCapacity(parkingLotRegistrationDTO.capacity());
        parkingLot.setAvailableSpaces(parkingLotRegistrationDTO.availableSpaces());

        parkingLot.setLocation(
                geometryFactory.createPoint(new Coordinate(parkingLotRegistrationDTO.latitude(), parkingLotRegistrationDTO.longitude()))
        );

        parkingLot = parkingLotRepository.save(parkingLot);

        // Create parking spaces for manual parking lots based on capacity
        if (parkingLotRegistrationDTO.source() == ParkingLot.Source.MANUAL && parkingLotRegistrationDTO.capacity() > 0) {
            createParkingSpacesForLot(parkingLot, parkingLotRegistrationDTO.capacity());
        }

        return mapToResponseDTO(parkingLot);
    }

    @Override
    public ParkingLotResponseDTO getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + id));
        return mapToResponseDTO(parkingLot);
    }

    @Override
    public ParkingLotResponseDTO updateParkingLot(Long id, ParkingLotRegistrationDTO parkingLotRegistrationDTO) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + id));

        updateParkingLotFields(parkingLot, parkingLotRegistrationDTO);
        parkingLot = parkingLotRepository.save(parkingLot);
        return mapToResponseDTO(parkingLot);
    }

    @Override
    public ParkingLotResponseDTO patchParkingLot(Long id, ParkingLotRegistrationDTO parkingLotRegistrationDTO) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + id));

        patchParkingLotFields(parkingLot, parkingLotRegistrationDTO);
        parkingLot = parkingLotRepository.save(parkingLot);
        return mapToResponseDTO(parkingLot);
    }

    @Override
    public void deleteParkingLot(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new ResourceNotFoundException("Parking lot not found with id: " + id);
        }
        parkingLotRepository.deleteById(id);
    }

    private void updateParkingLotFields(ParkingLot parkingLot, ParkingLotRegistrationDTO dto) {
        if (dto.source() != null) {
            parkingLot.setSource(dto.source());
        }
        if (dto.osmType() != null) {
            parkingLot.setOsmType(dto.osmType());
        }
        if (dto.osmId() != null) {
            parkingLot.setOsmId(dto.osmId());
        }
        if (dto.name() != null) {
            parkingLot.setName(dto.name());
        }
        if (dto.description() != null) {
            parkingLot.setDescription(dto.description());
        }
        if (dto.address() != null) {
            parkingLot.setAddress(dto.address());
        }
        parkingLot.setPublicAccess(dto.publicAccess());
        parkingLot.setHasChargingStations(dto.hasChargingStations());
        parkingLot.setHasDisabledAccess(dto.hasDisabledAccess());
        parkingLot.setHasCctv(dto.hasCctv());
        parkingLot.setCapacity(dto.capacity());
        parkingLot.setAvailableSpaces(dto.availableSpaces());
        if (dto.latitude() != null && dto.longitude() != null) {
            parkingLot.setLocation(
                    geometryFactory.createPoint(new Coordinate(dto.latitude(), dto.longitude()))
            );
        }
    }

    private void patchParkingLotFields(ParkingLot parkingLot, ParkingLotRegistrationDTO dto) {
        if (dto.name() != null) {
            parkingLot.setName(dto.name());
        }
        if (dto.description() != null) {
            parkingLot.setDescription(dto.description());
        }
        if (dto.address() != null) {
            parkingLot.setAddress(dto.address());
        }

        parkingLot.setPublicAccess(dto.publicAccess());
        parkingLot.setHasChargingStations(dto.hasChargingStations());
        parkingLot.setHasDisabledAccess(dto.hasDisabledAccess());
        parkingLot.setHasCctv(dto.hasCctv());
        parkingLot.setCapacity(dto.capacity());
        parkingLot.setAvailableSpaces(dto.availableSpaces());
        if (dto.latitude() != null && dto.longitude() != null) {
            parkingLot.setLocation(
                    geometryFactory.createPoint(new Coordinate(dto.latitude(), dto.longitude()))
            );
        }
    }

    private ParkingLotResponseDTO mapToResponseDTO(ParkingLot parkingLot) {
        Point location = parkingLot.getLocation();
        Double latitude = location != null ? location.getY() : null;
        Double longitude = location != null ? location.getX() : null;

        return new ParkingLotResponseDTO(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getDescription(),
                parkingLot.getAddress(),
                parkingLot.getPhone(),
                parkingLot.getEmail(),
                parkingLot.getOperatingHours(),
                parkingLot.getStatus() != null ? parkingLot.getStatus().name() : null,
                latitude,
                longitude,
                parkingLot.getPublicAccess(),
                parkingLot.getHasChargingStations(),
                parkingLot.getHasDisabledAccess(),
                parkingLot.getHasCctv(),
                parkingLot.getCovered(),
                parkingLot.getCapacity(),
                parkingLot.getAvailableSpaces(),
                parkingLot.getAvailabilityUpdatedAt(),
                parkingLot.getCreatedAt(),
                parkingLot.getUpdatedAt()
        );
    }

    /**
     * Creates parking spaces for a manual parking lot based on its capacity.
     * Each parking space inherits information from the parking lot.
     * 
     * @param parkingLot The parking lot to create spaces for
     * @param capacity The number of parking spaces to create
     */
    private void createParkingSpacesForLot(ParkingLot parkingLot, Integer capacity) {
        for (int i = 1; i <= capacity; i++) {
            ParkingSpace parkingSpace = new ParkingSpace();
            
            // Set the parking lot relationship
            parkingSpace.setParkingLot(parkingLot);
            
            // Inherit location from parking lot (same coordinates)
            parkingSpace.setLocation(parkingLot.getLocation());
            
            // Set default space type based on parking lot features
            ParkingSpace.SpaceType spaceType = determineSpaceType(parkingLot);
            parkingSpace.setSpaceType(spaceType);
            
            // Set default status to available
            parkingSpace.setSpaceStatus(ParkingSpace.SpaceStatus.AVAILABLE);
            
            // Create a label for the space
            parkingSpace.setLabel(generateSpaceLabel(i));
            
            // Create description based on parking lot information
            parkingSpace.setDescription(generateSpaceDescription(parkingLot, i));
            
            // Save the parking space
            parkingSpaceRepository.save(parkingSpace);
        }
    }

    /**
     * Determines the appropriate space type based on parking lot features.
     */
    private ParkingSpace.SpaceType determineSpaceType(ParkingLot parkingLot) {
        if (parkingLot.getHasChargingStations() != null && parkingLot.getHasChargingStations()) {
            return ParkingSpace.SpaceType.EV;
        } else if (parkingLot.getHasDisabledAccess() != null && parkingLot.getHasDisabledAccess()) {
            return ParkingSpace.SpaceType.DISABLED;
        } else {
            return ParkingSpace.SpaceType.STANDARD;
        }
    }

    /**
     * Generates a label for the parking space.
     */
    private String generateSpaceLabel(int spaceNumber) {
        return String.format("A%d", spaceNumber);
    }

    /**
     * Generates a description for the parking space based on parking lot information.
     */
    private String generateSpaceDescription(ParkingLot parkingLot, int spaceNumber) {
        StringBuilder description = new StringBuilder();
        description.append("Parking space ").append(spaceNumber).append(" at ").append(parkingLot.getName());
        
        if (parkingLot.getAddress() != null) {
            description.append(", ").append(parkingLot.getAddress());
        }
        
        if (parkingLot.getDescription() != null) {
            description.append(". ").append(parkingLot.getDescription());
        }
        
        return description.toString();
    }
    

    @Override
    @Transactional(readOnly = true)
    public ParkingLotDetailDTO getParkingLotDetailById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + id));
        
        return mapToParkingLotDetailDTO(parkingLot);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingLotSearchDTO> getAllParkingLotsForUsers(Pageable pageable) {
        return parkingLotRepository.findAll(pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingLotSearchDTO> findNearbyParkingLots(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        double radiusMeters = radiusKm * 1000; // Convert km to meters
        
        List<ParkingLot> nearbyLots = parkingLotRepository.findNearbyParkingLots(point, radiusMeters);
        
        List<ParkingLotSearchDTO> searchDTOs = nearbyLots.stream()
                .map(this::mapToParkingLotSearchDTO)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(searchDTOs, pageable, searchDTOs.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingLotSearchDTO> findAvailableParkingLots(Pageable pageable) {
        return parkingLotRepository.findAvailableParkingLots(pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingLotSearchDTO> findParkingLotsBySpaceType(ParkingSpace.SpaceType spaceType, Pageable pageable) {
        return parkingLotRepository.findParkingLotsBySpaceType(spaceType, pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingLotSearchDTO> findParkingLotsByFeatures(Boolean hasChargingStations, Boolean hasDisabledAccess, 
                                                             Boolean hasCctv, Boolean covered, Pageable pageable) {
        return parkingLotRepository.findParkingLotsByFeatures(hasChargingStations, hasDisabledAccess, hasCctv, covered, pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParkingLotSearchDTO> searchParkingLots(String searchTerm, Pageable pageable) {
        return parkingLotRepository.searchParkingLots(searchTerm, pageable)
                .map(this::mapToParkingLotSearchDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableSpacesCount(Long lotId) {
        return parkingSpaceRepository.findAvailableSpacesByLotId(lotId).size();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableSpacesCountByType(Long lotId, ParkingSpace.SpaceType spaceType) {
        return (int) parkingSpaceRepository.countAvailableSpacesByLotIdAndSpaceType(lotId, spaceType);
    }
    
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    
    // ==================== MAPPING METHODS ====================
    
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
    
    // ==================== REVIEWS AND IMAGES METHODS ====================
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewSummaryDTO> getParkingLotReviews(Long lotId, Pageable pageable) {
        // Validate parking lot exists
        parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + lotId));
        
        return reviewRepository.findByParkingLotId(lotId, pageable)
                .map(this::mapToReviewSummaryDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ParkingSpaceImageDTO> getParkingLotImages(Long lotId) {
        // Validate parking lot exists
        parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + lotId));
        
        // Get all parking spaces for this lot and extract their images
        List<ParkingSpace> spaces = parkingSpaceRepository.findByParkingLotId(lotId);
        
        return spaces.stream()
                .flatMap(space -> space.getImages().stream())
                .map(this::mapToParkingSpaceImageDTO)
                .collect(Collectors.toList());
    }
    

    
    private ParkingSpaceImageDTO mapToParkingSpaceImageDTO(ParkingSpaceImage image) {
        return new ParkingSpaceImageDTO(
                image.getId(),
                image.getParkingSpace() != null ? image.getParkingSpace().getId() : null,
                image.getUrl(),
                image.getDescription(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}
