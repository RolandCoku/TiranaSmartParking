package com.tirana.smartparking.parking.availability.repository;

import com.tirana.smartparking.parking.availability.events.AvailabilityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityEventRepository extends JpaRepository<AvailabilityEvent, Long> {
}
