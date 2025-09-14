package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    Optional<ParkingLot> findByOsmIdAndOsmType(Long osmId, String osmType);
    Optional<ParkingLot> findByNameAndAddress(String name, String address);
}
