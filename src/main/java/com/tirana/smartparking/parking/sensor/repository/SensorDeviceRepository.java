package com.tirana.smartparking.parking.sensor.repository;

import com.tirana.smartparking.parking.sensor.entity.SensorDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorDeviceRepository extends JpaRepository<SensorDevice, Long> {
      Optional<SensorDevice> findByDeviceId(String deviceId);
}
