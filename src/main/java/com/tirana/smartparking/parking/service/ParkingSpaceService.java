package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.parking.dto.ParkingSpaceRegistrationDTO;
import com.tirana.smartparking.parking.dto.ParkingSpaceResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ParkingSpaceService {
    ParkingSpaceResponseDTO registerParkingSpace(ParkingSpaceRegistrationDTO parkingSpaceRegistrationDTO);
    Page<ParkingSpaceResponseDTO> getAllParkingSpaces(Pageable pageable);
    ParkingSpaceResponseDTO getParkingSpaceById(Long id);
}
