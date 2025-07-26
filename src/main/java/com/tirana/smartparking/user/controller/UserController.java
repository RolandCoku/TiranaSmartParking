package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.common.util.SortParser;
import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public String updateUser(@PathVariable Long id) {
        // This method would typically update a user's information in the database
        return "User with ID: " + id + " updated!";
    }

    @PatchMapping("/{id}")
    public String patchUser(@PathVariable Long id) {
        // This method would typically partially update a user's information in the database
        return "User with ID: " + id + " patched!";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        // This method would typically delete a user from the database
        return "User with ID: " + id + " deleted!";
    }

    @GetMapping("/{id}/cars")
    public String getUserCars(@PathVariable Long id) {
        // This method would typically return a list of cars associated with a user by their ID
        return "List of cars for user ID: " + id;
    }

    @PostMapping("/{id}/roles")
    public String addUserRole(@PathVariable Long id, @RequestBody String role) {
        // This method would typically add a new role for a user by their ID
        return "Role added for user ID: " + id;
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    public String removeUserRole(@PathVariable Long id, @PathVariable Long roleId) {
        // This method would typically delete a role associated with a user by their ID
        return "Role with ID: " + roleId + " deleted for user ID: " + id;
    }
}
