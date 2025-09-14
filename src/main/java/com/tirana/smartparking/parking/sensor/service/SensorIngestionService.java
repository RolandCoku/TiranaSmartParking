package com.tirana.smartparking.parking.sensor.service;

import org.springframework.stereotype.Service;

import com.tirana.smartparking.parking.sensor.dto.SensorEventDTO;

@Service
public interface SensorIngestionService {
    void ingestSensorEvent(SensorEventDTO eventDTO);
}
