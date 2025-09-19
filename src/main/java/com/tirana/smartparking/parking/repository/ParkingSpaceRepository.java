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

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    long countByParkingLotAndSpaceStatus(ParkingLot parkingLot, ParkingSpace.SpaceStatus status);

    // Find parking spaces by lot ID
    List<ParkingSpace> findByParkingLotId(Long lotId);
    
    // Find available parking spaces by lot ID
    @Query("SELECT ps FROM ParkingSpace ps WHERE ps.parkingLot.id = :lotId AND ps.spaceStatus = 'AVAILABLE'")
    List<ParkingSpace> findAvailableSpacesByLotId(@Param("lotId") Long lotId);
    
    // Find parking spaces by space type and status
    @Query("SELECT ps FROM ParkingSpace ps WHERE ps.spaceType = :spaceType AND ps.spaceStatus = :status")
    Page<ParkingSpace> findBySpaceTypeAndStatus(@Param("spaceType") ParkingSpace.SpaceType spaceType, 
                                               @Param("status") ParkingSpace.SpaceStatus status, 
                                               Pageable pageable);
    
    // Find parking spaces within a radius
    @Query(value = "SELECT * FROM parking_spaces WHERE ST_DWithin(location, :point, :radiusMeters) AND space_status = 'AVAILABLE'", 
           nativeQuery = true)
    List<ParkingSpace> findNearbyAvailableSpaces(@Param("point") Point point, @Param("radiusMeters") double radiusMeters);
    
    // Find parking spaces by lot ID and space type
    @Query("SELECT ps FROM ParkingSpace ps WHERE ps.parkingLot.id = :lotId AND ps.spaceType = :spaceType")
    List<ParkingSpace> findByLotIdAndSpaceType(@Param("lotId") Long lotId, @Param("spaceType") ParkingSpace.SpaceType spaceType);
    
    // Count available spaces by lot ID and space type
    @Query("SELECT COUNT(ps) FROM ParkingSpace ps WHERE ps.parkingLot.id = :lotId AND ps.spaceType = :spaceType AND ps.spaceStatus = 'AVAILABLE'")
    long countAvailableSpacesByLotIdAndSpaceType(@Param("lotId") Long lotId, @Param("spaceType") ParkingSpace.SpaceType spaceType);
    
    // Find parking spaces with sensors
    @Query("SELECT ps FROM ParkingSpace ps WHERE ps.sensorDevice IS NOT NULL")
    Page<ParkingSpace> findSpacesWithSensors(Pageable pageable);
}

