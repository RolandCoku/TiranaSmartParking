package com.tirana.smartparking.parking.sensor.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.parking.sensor.dto.SensorDTO;
import com.tirana.smartparking.parking.sensor.dto.SensorEventDTO;
import com.tirana.smartparking.parking.sensor.dto.SensorRegistrationDTO;
import com.tirana.smartparking.parking.sensor.service.SensorDeviceService;
import com.tirana.smartparking.parking.sensor.service.SensorIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorDeviceController {

    private final SensorDeviceService sensorDeviceService;
    private final SensorIngestionService sensorIngestionService;

    public SensorDeviceController(SensorDeviceService sensorDeviceService, SensorIngestionService sensorIngestionService) {
        this.sensorDeviceService = sensorDeviceService;
        this.sensorIngestionService = sensorIngestionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SensorDTO>> registerSensorDevice(@Valid @RequestBody SensorRegistrationDTO sensorRegistrationDTO) {
        SensorDTO sensorDTO = sensorDeviceService.registerSensorDevice(sensorRegistrationDTO);
        return ResponseHelper.created("Sensor device registered successfully", sensorDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SensorDTO>> getSensorDeviceById(@PathVariable Long id) {

        return ResponseHelper.ok("Sensor device fetched successfully", sensorDeviceService.getSensorDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SensorDTO>> updateSensorDevice(@PathVariable Long id,
                                                                     @Valid @RequestBody SensorRegistrationDTO sensorRegistrationDTO) {
            SensorDTO sensorDTO = sensorDeviceService.updateSensorDevice(id, sensorRegistrationDTO);
            return ResponseHelper.ok("Sensor device updated successfully", sensorDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SensorDTO>> partiallyUpdateSensorDevice(@PathVariable Long id,
                                                                              @RequestBody SensorRegistrationDTO sensorRegistrationDTO) {
        SensorDTO sensorDTO = sensorDeviceService.patchSensorDevice(id, sensorRegistrationDTO);
        return ResponseHelper.ok("Sensor device partially updated successfully", sensorDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSensorDeviceById(@PathVariable Long id)
    {
        sensorDeviceService.deleteSensorDeviceById(id);
        return ResponseHelper.noContent();
    }

    @PostMapping("/event")
    public ResponseEntity<ApiResponse<Void>> ingestSensorEvent(@Valid @RequestBody SensorEventDTO sensorEventDTO) {
        sensorIngestionService.ingestSensorEvent(sensorEventDTO);
        return ResponseHelper.ok("Sensor event ingested successfully", null);
    }
}
