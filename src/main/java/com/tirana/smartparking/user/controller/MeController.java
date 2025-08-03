package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.common.util.SortParser;
import com.tirana.smartparking.user.dto.CarResponseDTO;
import com.tirana.smartparking.user.dto.UserCarsDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import com.tirana.smartparking.user.service.CarService;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final UserService userService;
    private final SortParser sortParser;
    private final CarService carService;

    public MeController(UserService userService, SortParser sortParser, CarService carService) {
        this.sortParser = sortParser;
        this.userService = userService;
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser(Authentication authentication) {
        UserResponseDTO currentUser = userService.findByUsername(authentication.getName());
        if (currentUser == null) {
            return ResponseHelper.notFound("Current user not found", null);
        }
        return ResponseHelper.ok("Current user fetched successfully", currentUser);
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<ApiResponse<UserCarsDTO>> getCurrentUserCarById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long carId
    ) {
        UserResponseDTO currentUser = userService.findByUsername(userDetails.getUsername());

        if (currentUser == null) {
            return ResponseHelper.notFound("Current user not found", null);
        }

        UserCarsDTO userCar = carService.getCarByUserAndId(currentUser.getId(), carId);

        if (userCar == null) {
            return ResponseHelper.notFound("Car not found for this user", null);
        }

        return ResponseHelper.ok("User's car fetched successfully", userCar);
    }

    @GetMapping("/cars")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserCarsDTO>>> getUserCars(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort
    ) {
        Sort parsedSort = sortParser.parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, parsedSort);

        UserResponseDTO currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return ResponseHelper.notFound("Current user not found", null);
        }

        Page<UserCarsDTO> userCarsPage = userService.getUserCars(currentUser.getId(), pageable);
        PaginatedResponse<UserCarsDTO> paginatedResponse = PaginationUtil.toPaginatedResponse(userCarsPage);

        if (paginatedResponse.hasContent())
            return ResponseHelper.ok("List of user's cars fetched successfully", paginatedResponse);
        else
            return ResponseHelper.ok("No cars found for this user", paginatedResponse);
    }

}
