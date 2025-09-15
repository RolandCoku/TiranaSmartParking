package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.parking.dto.ParkingSpaceRegistrationDTO;
import com.tirana.smartparking.parking.dto.ParkingSpaceResponseDTO;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.sensor.repository.SensorDeviceRepository;
import com.tirana.smartparking.parking.service.ParkingSpaceService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ParkingSpaceServiceImpl implements ParkingSpaceService {
    private final ParkingLotRepository parkingLotRepository;
    private final GeometryFactory geometryFactory;
    private final SensorDeviceRepository sensorRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    public ParkingSpaceServiceImpl(ParkingLotRepository parkingLotRepository, GeometryFactory geometryFactory, SensorDeviceRepository sensorRepository, ParkingSpaceRepository parkingSpaceRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.geometryFactory = geometryFactory;
        this.sensorRepository = sensorRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    @Override
    public Page<ParkingSpaceResponseDTO> getAllParkingSpaces(Pageable pageable) {
        return parkingSpaceRepository.findAll(pageable).map(this::mapToParkingSpaceResponseDTO);
    }

    @Override
    public ParkingSpaceResponseDTO getParkingSpaceById(Long id) {
        return parkingSpaceRepository.findById(id)
                .map(this::mapToParkingSpaceResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Parking space not found"));
    }

    @Override
    public ParkingSpaceResponseDTO registerParkingSpace(ParkingSpaceRegistrationDTO parkingSpaceRegistrationDTO) {
        ParkingSpace parkingSpace = new ParkingSpace();

        if (parkingSpaceRegistrationDTO.parkingLotId() != null) {
            parkingSpace.setParkingLot(parkingLotRepository.findById(parkingSpaceRegistrationDTO.parkingLotId())
                    .orElseThrow(() -> new IllegalArgumentException("Parking lot not found")));
        } else if (parkingSpaceRegistrationDTO.latitude() != null && parkingSpaceRegistrationDTO.longitude() != null) {
            parkingSpace.setLocation(geometryFactory.createPoint(new Coordinate(parkingSpaceRegistrationDTO.longitude(), parkingSpaceRegistrationDTO.latitude())));
        } else {
            throw new IllegalArgumentException("Either parking lot ID or coordinates must be provided");
        }

        parkingSpace.setSpaceType(ParkingSpace.SpaceType.valueOf(parkingSpaceRegistrationDTO.spaceType().toUpperCase()));
        parkingSpace.setSpaceStatus(ParkingSpace.SpaceStatus.valueOf(parkingSpaceRegistrationDTO.spaceStatus().toUpperCase()));
        parkingSpace.setLabel(parkingSpaceRegistrationDTO.label());
        parkingSpace.setDescription(parkingSpaceRegistrationDTO.description());

        if (parkingSpaceRegistrationDTO.sensorId() != null) {
            parkingSpace.setSensorDevice(sensorRepository.findById(parkingSpaceRegistrationDTO.sensorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found")));
        }

        return mapToParkingSpaceResponseDTO(parkingSpaceRepository.save(parkingSpace));
    }

    //TODO: This needs to be reviewed
    private ParkingSpaceResponseDTO mapToParkingSpaceResponseDTO(ParkingSpace parkingSpace) {
        return new ParkingSpaceResponseDTO(
                parkingSpace.getId(),
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getId() : null,
                parkingSpace.getLocation() != null ? parkingSpace.getLocation().getX() : null,
                parkingSpace.getLocation() != null ? parkingSpace.getLocation().getY() : null,
                parkingSpace.getSpaceType().name(),
                parkingSpace.getSpaceStatus().name(),
                parkingSpace.getLabel(),
                parkingSpace.getDescription(),
                parkingSpace.getSensorDevice() != null ? parkingSpace.getSensorDevice().getId() : null,
                parkingSpace.getCreatedAt(),
                parkingSpace.getUpdatedAt()
        );
    }
}

