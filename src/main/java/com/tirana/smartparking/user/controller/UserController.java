package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.common.util.SortParser;
import com.tirana.smartparking.user.dto.*;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final SortParser sortParser;

    public UserController(UserService userService, SortParser sortParser) {
        this.userService = userService;
        this.sortParser = sortParser;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponseDTO>>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sortBy
    ) {

        Sort sort = sortParser.parseSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponseDTO> userPage = userService.getAllUsers(pageable);
        PaginatedResponse<UserResponseDTO> paginatedResponse = PaginationUtil.toPaginatedResponse(userPage);

        if (paginatedResponse.hasContent())
            return ResponseHelper.ok("List of users fetched successfully", paginatedResponse);
        else
            return ResponseHelper.ok("No users found", paginatedResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findById(id);
        return ResponseHelper.ok("User fetched successfully", user);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO responseDTO = userService.createUser(userCreateDTO);
        return ResponseHelper.created("User created successfully", responseDTO);
    }

    //Update a user's information
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseHelper.ok("User updated successfully", updatedUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> patchUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO patchedUser = userService.patchUser(id, userUpdateDTO);
        return ResponseHelper.ok("User patched successfully", patchedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseHelper.noContent();
    }

    @GetMapping("/{id}/cars")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserCarsDTO>>> getUserCars(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort
    ) {
        Sort parsedSort = sortParser.parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, parsedSort);

        Page<UserCarsDTO> userCarsPage = userService.getUserCars(id, pageable);
        PaginatedResponse<UserCarsDTO> paginatedResponse = PaginationUtil.toPaginatedResponse(userCarsPage);

        if (paginatedResponse.hasContent())
            return ResponseHelper.ok("List of user's cars fetched successfully", paginatedResponse);
        else
            return ResponseHelper.ok("No cars found for this user", paginatedResponse);
    }

    @PostMapping("/{id}/cars")
    public ResponseEntity<ApiResponse<UserCarsDTO>> addUserCar(@PathVariable Long id, @RequestBody CarCreateDTO carCreateDTO) {
        UserCarsDTO userCars = userService.addCarToUser(id, carCreateDTO);
        return ResponseHelper.ok("Car added successfully", userCars);
    }

    @DeleteMapping("/{id}/cars/{carId}")
    public ResponseEntity<ApiResponse<String>> removeUserCar(@PathVariable Long id, @PathVariable Long carId) {
        userService.removeCarFromUser(id, carId);
        return ResponseHelper.noContent();
    }

    @PatchMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponseDTO>> addUserRole(@PathVariable Long id, @RequestBody Set<String> roles) {
        UserResponseDTO user = userService.addRoleToUser(id, roles);
        return ResponseHelper.ok("Role added successfully", user);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> removeUserRole(@PathVariable Long id, @PathVariable String roleName) {
        UserResponseDTO user = userService.removeRoleFromUser(id, roleName);
        return ResponseHelper.ok("Role removed successfully", user);
    }
}
