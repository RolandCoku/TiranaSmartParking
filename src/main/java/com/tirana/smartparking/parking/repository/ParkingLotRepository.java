package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    Optional<ParkingLot> findByOsmIdAndOsmType(Long osmId, String osmType);
    Optional<ParkingLot> findByNameAndAddress(String name, String address);
    
    // Find parking lots within a radius (in meters)
    @Query(value = "SELECT * FROM parking_lots WHERE ST_DWithin(location, :point, :radiusMeters) AND status = 'ACTIVE'", 
           nativeQuery = true)
    List<ParkingLot> findNearbyParkingLots(@Param("point") Point point, @Param("radiusMeters") double radiusMeters);
    
    // Find parking lots with available spaces
    @Query("SELECT pl FROM ParkingLot pl WHERE pl.status = 'ACTIVE' AND pl.availableSpaces > 0")
    Page<ParkingLot> findAvailableParkingLots(Pageable pageable);
    
    // Find parking lots by space type availability
    @Query("SELECT DISTINCT pl FROM ParkingLot pl JOIN pl.parkingSpaces ps WHERE pl.status = 'ACTIVE' AND ps.spaceType = :spaceType AND ps.spaceStatus = 'AVAILABLE'")
    Page<ParkingLot> findParkingLotsBySpaceType(@Param("spaceType") ParkingSpace.SpaceType spaceType, Pageable pageable);
    
    // Find parking lots with specific features
    @Query("SELECT pl FROM ParkingLot pl WHERE pl.status = 'ACTIVE' AND " +
           "(:hasChargingStations IS NULL OR pl.hasChargingStations = :hasChargingStations) AND " +
           "(:hasDisabledAccess IS NULL OR pl.hasDisabledAccess = :hasDisabledAccess) AND " +
           "(:hasCctv IS NULL OR pl.hasCctv = :hasCctv) AND " +
           "(:covered IS NULL OR pl.covered = :covered)")
    Page<ParkingLot> findParkingLotsByFeatures(@Param("hasChargingStations") Boolean hasChargingStations,
                                              @Param("hasDisabledAccess") Boolean hasDisabledAccess,
                                              @Param("hasCctv") Boolean hasCctv,
                                              @Param("covered") Boolean covered,
                                              Pageable pageable);
    
    // Search parking lots by name or address
    @Query("SELECT pl FROM ParkingLot pl WHERE pl.status = 'ACTIVE' AND " +
           "(LOWER(pl.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pl.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<ParkingLot> searchParkingLots(@Param("searchTerm") String searchTerm, Pageable pageable);
}
