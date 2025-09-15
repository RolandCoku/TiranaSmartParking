package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.parking.dto.ParkingLotRegistrationDTO;
import com.tirana.smartparking.parking.dto.ParkingLotResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ParkingLotService {
    ParkingLotResponseDTO registerParkingLot(ParkingLotRegistrationDTO parkingLotRegistrationDTO);
    ParkingLotResponseDTO getParkingLotById(Long id);
    Page<ParkingLotResponseDTO> getAllParkingLots(Pageable pageable);
}
