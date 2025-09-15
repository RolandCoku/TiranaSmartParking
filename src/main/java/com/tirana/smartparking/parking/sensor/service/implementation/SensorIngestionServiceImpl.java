package com.tirana.smartparking.parking.sensor.service.implementation;

import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.parking.availability.AvailabilityStream;
import com.tirana.smartparking.parking.availability.events.AvailabilityEvent;
import com.tirana.smartparking.parking.availability.repository.AvailabilityEventRepository;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.sensor.dto.SensorEventDTO;
import com.tirana.smartparking.parking.sensor.entity.SensorDevice;
import com.tirana.smartparking.parking.sensor.repository.SensorDeviceRepository;
import com.tirana.smartparking.parking.sensor.service.SensorIngestionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SensorIngestionServiceImpl implements SensorIngestionService {
    private final SensorDeviceRepository sensorDeviceRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final AvailabilityEventRepository availabilityEventRepository;
    private final AvailabilityStream availabilityStream;

    public SensorIngestionServiceImpl(
            SensorDeviceRepository sensorDeviceRepository,
            ParkingSpaceRepository parkingSpaceRepository,
            AvailabilityEventRepository availabilityEventRepository,
            AvailabilityStream availabilityStream) {
        this.sensorDeviceRepository = sensorDeviceRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.availabilityEventRepository = availabilityEventRepository;
        this.availabilityStream = availabilityStream;
    }

    @Override
    @Transactional
    public void ingestSensorEvent(SensorEventDTO eventDTO) {
        // Validate sensor
        SensorDevice sensor = sensorDeviceRepository.findByDeviceId(eventDTO.deviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found: " + eventDTO.deviceId()));
        if (!sensor.getApiKey().equals(eventDTO.apiKey())) {
            throw new SecurityException("Invalid API key for sensor: " + eventDTO.deviceId());
        }
        // Validate parking space
        ParkingSpace space = parkingSpaceRepository.findById(eventDTO.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found: " + eventDTO.spaceId()));
        // Update space status
        ParkingSpace.SpaceStatus newStatus = null;
        if ("OCCUPIED".equalsIgnoreCase(eventDTO.event())) {
            newStatus = ParkingSpace.SpaceStatus.OCCUPIED;
        } else if ("AVAILABLE".equalsIgnoreCase(eventDTO.event()) || "FREE".equalsIgnoreCase(eventDTO.event())) {
            newStatus = ParkingSpace.SpaceStatus.AVAILABLE;
        }

        if (newStatus != null) {
            space.setSpaceStatus(newStatus);
            parkingSpaceRepository.save(space);
        }
        // Calculate available spaces in the lot
        var lot = space.getParkingLot();
        long availableSpaces = lot.getAvailableSpaces();

        // Persist event
        AvailabilityEvent event = new AvailabilityEvent();
        event.setParkingLot(lot);
        event.setParkingSpace(space);
        event.setAvailableSpaces((int) availableSpaces);
        event.setEvent(eventDTO.event());
        event.setCreatedAt(eventDTO.timestamp() != null ? eventDTO.timestamp() : Instant.now());
        event.setUpdatedAt(Instant.now());
        event.setSource("SENSOR");
        availabilityEventRepository.save(event);
        // Publish update
        availabilityStream.publish(lot.getId(), availableSpaces);
    }
}
