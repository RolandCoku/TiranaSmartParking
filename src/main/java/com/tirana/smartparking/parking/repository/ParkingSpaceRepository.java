package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
}
