package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.parking.dto.ParkingLotResponseDTO;
import com.tirana.smartparking.parking.dto.ParkingSpaceResponseDTO;
import org.locationtech.jts.geom.Point;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/v1/parking")
public class ParkingController {

    //TODO: Implement this method to return actual parking lots at the given location
    public List<ParkingLotResponseDTO> getParkingLotsAtLocation(Point point) {
        return List.of();
    }

    //TODO: Implement this method to return actual parking spaces at the given location
    public List<ParkingSpaceResponseDTO> getParkingSpacesAtLocation(Point point) {
        return List.of();
    }

    //TODO: Implement this method to return actual available parking spaces at the given location
    public List<ParkingSpaceResponseDTO> getAvailableParkingSpacesAtLocation(Point point) {
        return List.of();
    }

}
