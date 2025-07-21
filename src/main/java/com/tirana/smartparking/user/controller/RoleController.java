package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.entity.Role;
import com.tirana.smartparking.user.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    // This controller will handle role-related operations,
    // For example, getting all roles, creating roles, etc.

    private RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ResponseHelper.ok("List of roles fetched successfully", roleService.getRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(Long id) {
        return ResponseHelper.ok("Role fetched successfully", roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody RoleDTO roleDTO) {
        Role createdRole = roleService.createRole(roleDTO);
        return ResponseHelper.created("Role created successfully", createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRole(@PathVariable Long id) {
        // This method would typically update a role's information
        // For demonstration purposes, we are returning a dummy response
        return ResponseEntity.ok("Role with ID: " + id + " updated!");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> patchRole(@PathVariable Long id) {
        // This method would typically partially update a role's information
        // For demonstration purposes, we are returning a dummy response
        return ResponseEntity.ok("Role with ID: " + id + " patched!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        // This method would typically delete a role from the system
        // For demonstration purposes, we are returning a dummy response
        return ResponseEntity.ok("Role with ID: " + id + " deleted!");
    }
}
