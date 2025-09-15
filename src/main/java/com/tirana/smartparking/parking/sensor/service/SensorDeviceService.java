package com.tirana.smartparking.parking.sensor.service;

import com.tirana.smartparking.parking.sensor.dto.SensorDTO;
import com.tirana.smartparking.parking.sensor.dto.SensorRegistrationDTO;
import org.springframework.stereotype.Service;

@Service
public interface SensorDeviceService {
    SensorDTO registerSensorDevice(SensorRegistrationDTO sensorRegistrationDTO);
    SensorDTO getSensorDeviceById(Long id);
    SensorDTO updateSensorDevice(Long id, SensorRegistrationDTO sensorRegistrationDTO);
    SensorDTO patchSensorDevice(Long id, SensorRegistrationDTO sensorRegistrationDTO);
    void deleteSensorDeviceById(Long id);

}
