package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {

        List<UserResponseDTO> userResponse = userService.getAllUsers();

        if (userResponse.isEmpty())
            return ResponseHelper.ok("No users found", userResponse);
        else
            return ResponseHelper.ok("List of users fetched successfully", userResponse);
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id) {
        // This method would typically return a user by their ID from the database
        return "User details for ID: " + id;
    }

    @PostMapping
    public String createUser(@RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO responseDTO = userService.createUser(userCreateDTO);
        return "User created!";
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

    @GetMapping("/{id}/roles")
    public String getUserRoles(@PathVariable Long id) {
        // This method would typically return a list of roles associated with a user by their ID
        return "List of roles for user ID: " + id;
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
