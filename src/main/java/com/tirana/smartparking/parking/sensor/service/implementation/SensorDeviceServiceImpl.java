package com.tirana.smartparking.parking.sensor.service.implementation;

import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.sensor.dto.SensorDTO;
import com.tirana.smartparking.parking.sensor.dto.SensorRegistrationDTO;
import com.tirana.smartparking.parking.sensor.entity.SensorDevice;
import com.tirana.smartparking.parking.sensor.repository.SensorDeviceRepository;
import com.tirana.smartparking.parking.sensor.service.SensorDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class SensorDeviceServiceImpl implements SensorDeviceService {
    SensorDeviceRepository sensorDeviceRepository;
    ParkingLotRepository parkingLotRepository;
    ParkingSpaceRepository parkingSpaceRepository;

    public SensorDeviceServiceImpl(SensorDeviceRepository sensorDeviceRepository,
                                   ParkingLotRepository parkingLotRepository,
                                   ParkingSpaceRepository parkingSpaceRepository) {
        this.sensorDeviceRepository = sensorDeviceRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    @Override
    public SensorDTO getSensorDeviceById(Long id) {
        SensorDevice sensorDevice = sensorDeviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sensor with this ID does not exist"));

        return mapToSensorDTO(sensorDevice);
    }

    @Override
    public SensorDTO registerSensorDevice(SensorRegistrationDTO sensorRegistrationDTO) {

        if (sensorDeviceRepository.findByDeviceId(sensorRegistrationDTO.deviceId()).isPresent()) {
            throw new IllegalArgumentException("Sensor with this device ID already exists");
        }

        if (sensorRegistrationDTO.parkingLotId() == null && sensorRegistrationDTO.parkingSpaceId() == null) {
            throw new IllegalArgumentException("Either parking lot ID or parking space ID must be provided");
        }

        SensorDevice sensorDevice = new SensorDevice();

        sensorDevice.setDeviceId(sensorRegistrationDTO.deviceId());
        sensorDevice.setApiKey(sensorRegistrationDTO.apiKey());
        sensorDevice.setSensorType(SensorDevice.SensorType.valueOf(sensorRegistrationDTO.sensorType().toUpperCase()));

        if (sensorRegistrationDTO.parkingLotId() != null)
            sensorDevice.setParkingLot(parkingLotRepository.findById(sensorRegistrationDTO.parkingLotId())
                .orElse(null));

        if (sensorRegistrationDTO.parkingSpaceId() != null)
            sensorDevice.setParkingSpace(parkingSpaceRepository.findById(sensorRegistrationDTO.parkingSpaceId())
                .orElse(null));

        sensorDevice.setDescription(sensorRegistrationDTO.description());

        sensorDevice = sensorDeviceRepository.save(sensorDevice);

        return mapToSensorDTO(sensorDevice);
    }

    //TODO: This needs to be reviewed I think I should allow for null values except for required fields to differentiate between update and patch
    @Override
    public SensorDTO updateSensorDevice(Long id, SensorRegistrationDTO sensorRegistrationDTO) {
        SensorDevice sensorDevice = sensorDeviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sensor with this ID does not exist"));

        if (sensorRegistrationDTO.deviceId() != null && !sensorRegistrationDTO.deviceId().equals(sensorDevice.getDeviceId())) {
            if (sensorDeviceRepository.findByDeviceId(sensorRegistrationDTO.deviceId()).isPresent()) {
                throw new IllegalArgumentException("Sensor with this device ID already exists");
            }
            sensorDevice.setDeviceId(sensorRegistrationDTO.deviceId());
        }

        if (sensorRegistrationDTO.apiKey() != null) {
            sensorDevice.setApiKey(sensorRegistrationDTO.apiKey());
        }
        if (sensorRegistrationDTO.sensorType() != null) {
            sensorDevice.setSensorType(SensorDevice.SensorType.valueOf(sensorRegistrationDTO.sensorType().toUpperCase()));
        }
        if (sensorRegistrationDTO.parkingLotId() != null) {
            sensorDevice.setParkingLot(parkingLotRepository.findById(sensorRegistrationDTO.parkingLotId())
                    .orElse(null));
        }
        if (sensorRegistrationDTO.parkingSpaceId() != null) {
            sensorDevice.setParkingSpace(parkingSpaceRepository.findById(sensorRegistrationDTO.parkingSpaceId())
                    .orElse(null));
        }
        if (sensorRegistrationDTO.description() != null) {
            sensorDevice.setDescription(sensorRegistrationDTO.description());
        }
        sensorDevice = sensorDeviceRepository.save(sensorDevice);
        return mapToSensorDTO(sensorDevice);
    }

    @Override
    public SensorDTO patchSensorDevice(Long id, SensorRegistrationDTO sensorRegistrationDTO) {
        SensorDevice sensorDevice = sensorDeviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sensor with this ID does not exist"));

        if (sensorRegistrationDTO.deviceId() != null && !sensorRegistrationDTO.deviceId().equals(sensorDevice.getDeviceId())) {
            if (sensorDeviceRepository.findByDeviceId(sensorRegistrationDTO.deviceId()).isPresent()) {
                throw new IllegalArgumentException("Sensor with this device ID already exists");
            }
            sensorDevice.setDeviceId(sensorRegistrationDTO.deviceId());
        }

        if (sensorRegistrationDTO.apiKey() != null) {
            sensorDevice.setApiKey(sensorRegistrationDTO.apiKey());
        }
        if (sensorRegistrationDTO.sensorType() != null) {
            sensorDevice.setSensorType(SensorDevice.SensorType.valueOf(sensorRegistrationDTO.sensorType().toUpperCase()));
        }
        if (sensorRegistrationDTO.parkingLotId() != null) {
            sensorDevice.setParkingLot(parkingLotRepository.findById(sensorRegistrationDTO.parkingLotId())
                    .orElse(null));
        }
        if (sensorRegistrationDTO.parkingSpaceId() != null) {
            sensorDevice.setParkingSpace(parkingSpaceRepository.findById(sensorRegistrationDTO.parkingSpaceId())
                    .orElse(null));
        }
        if (sensorRegistrationDTO.description() != null) {
            sensorDevice.setDescription(sensorRegistrationDTO.description());
        }
        sensorDevice = sensorDeviceRepository.save(sensorDevice);
        return mapToSensorDTO(sensorDevice);
    }

    @Override
    public void deleteSensorDeviceById(Long id) {
        if (!sensorDeviceRepository.existsById(id)) {
            throw new IllegalArgumentException("Sensor with this ID does not exist");
        }
        sensorDeviceRepository.deleteById(id);
    }


    //TODO: This needs to be reviewed
    private SensorDTO mapToSensorDTO(SensorDevice sensorDevice) {
        return new SensorDTO(
                sensorDevice.getId(),
                sensorDevice.getDeviceId(),
                sensorDevice.getApiKey(),
                sensorDevice.getParkingLot() != null ? sensorDevice.getParkingLot().getId() : null,
                sensorDevice.getParkingSpace() != null ? sensorDevice.getParkingSpace().getId() : null,
                sensorDevice.getSensorType().name(),
                sensorDevice.getStatus().name(),
                sensorDevice.getDescription(),
                sensorDevice.getLastSeenAt(),
                sensorDevice.getFirmware(),
                sensorDevice.getBatteryLevel() != null ? sensorDevice.getBatteryLevel() : 0,
                sensorDevice.getCreatedAt(),
                sensorDevice.getUpdatedAt()
        );
    }
}
