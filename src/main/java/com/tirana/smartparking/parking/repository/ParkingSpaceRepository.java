package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    long countByParkingLotAndSpaceStatus(ParkingLot parkingLot, ParkingSpace.SpaceStatus status);

//    List<ParkingSpace> findByLotId(Long lotId);
}

