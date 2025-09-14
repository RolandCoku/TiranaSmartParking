package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.parking.dto.ParkingLotRegistrationDTO;
import com.tirana.smartparking.parking.dto.ParkingLotResponseDTO;
import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.service.ParkingLotService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    private final ParkingLotRepository parkingLotRepository;
    private final GeometryFactory geometryFactory;

    public ParkingLotServiceImpl(ParkingLotRepository parkingLotRepository, GeometryFactory geometryFactory) {
        this.parkingLotRepository = parkingLotRepository;
        this.geometryFactory = geometryFactory;
    }

    @Override
    public Page<ParkingLotResponseDTO> getAllParkingLots(Pageable pageable) {
        return parkingLotRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public ParkingLotResponseDTO registerParkingLot(ParkingLotRegistrationDTO parkingLotRegistrationDTO) {

        ParkingLot parkingLot = new ParkingLot();

        if (parkingLotRegistrationDTO.source() == ParkingLot.Source.OSM) {
            if (parkingLotRegistrationDTO.osmId() == null || parkingLotRegistrationDTO.osmType() == null) {
                throw new IllegalArgumentException("OSM ID and OSM type must be provided for OSM source");
            }
            if (parkingLotRepository.findByOsmIdAndOsmType(parkingLotRegistrationDTO.osmId(), parkingLotRegistrationDTO.osmType()).isPresent()) {
                throw new IllegalArgumentException("Parking lot with this OSM ID and type already exists");
            }

            parkingLot.setSource(ParkingLot.Source.OSM);
            parkingLot.setOsmId(parkingLotRegistrationDTO.osmId());
            parkingLot.setOsmType(parkingLotRegistrationDTO.osmType());

        } else if (parkingLotRegistrationDTO.source() == ParkingLot.Source.MANUAL) {
            parkingLot.setSource(ParkingLot.Source.MANUAL);
        } else {
            throw new IllegalArgumentException("Invalid source type");
        }

        parkingLot.setName(parkingLotRegistrationDTO.name());
        parkingLot.setAddress(parkingLotRegistrationDTO.address());
        parkingLot.setDescription(parkingLotRegistrationDTO.description());
        parkingLot.setPublicAccess(parkingLotRegistrationDTO.publicAccess());
        parkingLot.setHasChargingStations(parkingLotRegistrationDTO.hasChargingStations());
        parkingLot.setHasDisabledAccess(parkingLotRegistrationDTO.hasDisabledAccess());
        parkingLot.setHasCctv(parkingLotRegistrationDTO.hasCctv());

        parkingLot.setCapacity(parkingLotRegistrationDTO.capacity());
        parkingLot.setAvailableSpaces(parkingLotRegistrationDTO.availableSpaces());

        parkingLot.setLocation(
                geometryFactory.createPoint(new Coordinate(parkingLotRegistrationDTO.latitude(), parkingLotRegistrationDTO.longitude()))
        );

        parkingLot = parkingLotRepository.save(parkingLot);

        return mapToResponseDTO(parkingLot);
    }

    @Override
    public ParkingLotResponseDTO getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot not found"));
        return mapToResponseDTO(parkingLot);
    }

    //TODO: Need to add more fields to the response DTO
    private ParkingLotResponseDTO mapToResponseDTO(ParkingLot parkingLot) {
        return new ParkingLotResponseDTO(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getLocation().toString(),
                parkingLot.getAddress(),
                parkingLot.getCapacity(),
                parkingLot.getAvailableSpaces(),
                parkingLot.getStatus().name(),
                parkingLot.getCreatedAt(),
                parkingLot.getUpdatedAt()
        );
    }
}
