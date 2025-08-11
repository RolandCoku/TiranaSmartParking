package com.tirana.smartparking.user.controller;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.common.util.SortParser;
import com.tirana.smartparking.user.dto.CarCreateDTO;
import com.tirana.smartparking.user.dto.CarResponseDTO;
import com.tirana.smartparking.user.dto.UserCarsDTO;
import com.tirana.smartparking.user.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;
    private final SortParser sortParser;

    public CarController(CarService carService, SortParser sortParser) {
        this.carService = carService;
        this.sortParser = sortParser;
    }

    // This controller will handle car-related operations,
    // For example, adding a car, deleting a car, getting all cars, etc.

    @PreAuthorize("hasAuthority('CAR_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<CarResponseDTO>>> getAllCars(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy
    ) {

        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CarResponseDTO> carPage = carService.getAllCars(pageable);

        PaginatedResponse<CarResponseDTO> response = PaginationUtil.toPaginatedResponse(carPage);

        return ResponseEntity.ok(new ApiResponse<>(true, "List of cars fetched successfully", response));
    }

    @PreAuthorize("hasAuthority('CAR_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarResponseDTO>> getCarById(@PathVariable Long id) {
        CarResponseDTO car = carService.findById(id);
        return ResponseHelper.ok("Car fetched successfully", car);
    }

    // This method will handle the addition of a new car for the authenticated user.
    @PreAuthorize("hasAuthority('CAR_CREATE')")
    @PostMapping
    public ResponseEntity<ApiResponse<CarResponseDTO>> addCar(
            @RequestBody CarCreateDTO carCreateDTO
            ) {
        CarResponseDTO createdCar = carService.createCar(carCreateDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Car added successfully", createdCar));
    }

    @PreAuthorize("hasAuthority('CAR_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserCarsDTO>> updateCar(@PathVariable Long id, @RequestBody CarCreateDTO carCreateDTO) {
        UserCarsDTO updatedCar = carService.updateCar(id, carCreateDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Car updated successfully", updatedCar));
    }

    @PreAuthorize("hasAuthority('CAR_UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserCarsDTO>> patchCar(@PathVariable Long id, @RequestBody CarCreateDTO carCreateDTO) {
        UserCarsDTO patchedCar = carService.patchCar(id, carCreateDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Car patched successfully", patchedCar));
    }

    @PreAuthorize("hasAuthority('CAR_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseHelper.noContent();
    }

}
