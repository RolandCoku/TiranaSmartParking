package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    // Define custom query methods if needed
    // For example, find by name, location, etc.
}
