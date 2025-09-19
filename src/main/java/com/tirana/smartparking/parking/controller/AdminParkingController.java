package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.common.util.SortParser;
import com.tirana.smartparking.parking.dto.ParkingLotRegistrationDTO;
import com.tirana.smartparking.parking.dto.ParkingLotResponseDTO;
import com.tirana.smartparking.parking.dto.ParkingSpaceRegistrationDTO;
import com.tirana.smartparking.parking.dto.ParkingSpaceResponseDTO;
import com.tirana.smartparking.parking.service.ParkingLotService;
import com.tirana.smartparking.parking.service.ParkingSpaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/parking")
class AdminParkingController {
    private final ParkingLotService parkingLotService;
    private final ParkingSpaceService parkingSpaceService;
    private final SortParser sortParser;

    public AdminParkingController(ParkingLotService parkingLotService, ParkingSpaceService parkingSpaceService, SortParser sortParser) {
        this.parkingLotService = parkingLotService;
        this.parkingSpaceService = parkingSpaceService;
        this.sortParser = sortParser;
    }

    @PreAuthorize("hasAuthority('PARKING_LOT_CREATE')")
    @PostMapping("/lots")
    public ResponseEntity<ApiResponse<ParkingLotResponseDTO>> registerParkingLot(@Validated @RequestBody ParkingLotRegistrationDTO parkingLotRegistrationDTO) {
        return ResponseHelper.created("Parking lot registered successfully", parkingLotService.registerParkingLot(parkingLotRegistrationDTO));
    }

    @PreAuthorize("hasAuthority('PARKING_SPACE_CREATE')")
    @PostMapping("/spaces")
    public ResponseEntity<ApiResponse<ParkingSpaceResponseDTO>> registerParkingSpace(@Validated @RequestBody ParkingSpaceRegistrationDTO parkingSpaceRegistrationDTO) {
        return ResponseHelper.created("Parking space registered successfully", parkingSpaceService.registerParkingSpace(parkingSpaceRegistrationDTO));
    }

    @PreAuthorize("hasAuthority('PARKING_LOT_READ')")
    @GetMapping("/lots/{id}")
    public ResponseEntity<ApiResponse<ParkingLotResponseDTO>> getParkingLotById(@PathVariable Long id) {
        return ResponseHelper.ok("Parking lot fetched successfully", parkingLotService.getParkingLotById(id));
    }

    @PreAuthorize("hasAuthority('PARKING_SPACE_READ')")
    @GetMapping("/spaces/{id}")
    public ResponseEntity<ApiResponse<ParkingSpaceResponseDTO>> getParkingSpaceById(@PathVariable Long id) {
        return ResponseHelper.ok("Parking space fetched successfully", parkingSpaceService.getParkingSpaceById(id));
    }

    @PreAuthorize("hasAuthority('PARKING_SPACE_READ')")
    @GetMapping("/spaces")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSpaceResponseDTO>>> getParkingSpaces(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy

    ) {
        Sort sort = sortParser.parseSort(sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ParkingSpaceResponseDTO> spacesPage = parkingSpaceService.getAllParkingSpaces(pageable);

        PaginatedResponse<ParkingSpaceResponseDTO> response = PaginationUtil.toPaginatedResponse(spacesPage);

        return ResponseHelper.ok("List of parking spaces fetched successfully", response);
    }

    @PreAuthorize("hasAuthority('PARKING_LOT_READ')")
    @GetMapping("/lots")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingLotResponseDTO>>> getParkingLots(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort
    ) {
        Sort sortObj = sortParser.parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<ParkingLotResponseDTO> lotsPage = parkingLotService.getAllParkingLots(pageable);

        PaginatedResponse<ParkingLotResponseDTO> response = PaginationUtil.toPaginatedResponse(lotsPage);

        return ResponseHelper.ok("List of parking lots fetched successfully", response);
    }

    @PreAuthorize("hasAuthority('PARKING_LOT_UPDATE')")
    @PutMapping("/lots/{id}")
    public ResponseEntity<ApiResponse<ParkingLotResponseDTO>> updateParkingLot(@PathVariable Long id, @Validated @RequestBody ParkingLotRegistrationDTO parkingLotRegistrationDTO) {
        return ResponseHelper.ok("Parking lot updated successfully", parkingLotService.updateParkingLot(id, parkingLotRegistrationDTO));
    }

    @PreAuthorize("hasAuthority('PARKING_LOT_UPDATE')")
    @PatchMapping("/lots/{id}")
    public ResponseEntity<ApiResponse<ParkingLotResponseDTO>> patchParkingLot(@PathVariable Long id, @RequestBody ParkingLotRegistrationDTO parkingLotRegistrationDTO) {
        return ResponseHelper.ok("Parking lot patched successfully", parkingLotService.patchParkingLot(id, parkingLotRegistrationDTO));
    }

    @PreAuthorize("hasAuthority('PARKING_LOT_DELETE')")
    @DeleteMapping("/lots/{id}")
    public ResponseEntity<?> deleteParkingLot(@PathVariable Long id) {
        parkingLotService.deleteParkingLot(id);
        return ResponseHelper.noContent();
    }
}
