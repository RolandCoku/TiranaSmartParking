package com.tirana.smartparking.user.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.security.PermissionEnum;
import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.dto.RoleResponseDTO;
import com.tirana.smartparking.user.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('EDIT_ROLES')")
@RequestMapping("/api/v1/roles")
public class RoleController {
    // This controller will handle role-related operations,
    // For example, getting all roles, creating roles, etc.

    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getAllRoles() {
        return ResponseHelper.ok("List of roles fetched successfully", roleService.getRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> getRoleById(@PathVariable Long id) {
        return ResponseHelper.ok("Role fetched successfully", roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDTO>> createRole(@RequestBody @Valid RoleDTO roleDTO) {
        RoleResponseDTO createdRole = roleService.createRole(roleDTO);
        return ResponseHelper.created("Role created successfully", createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> updateRole(@PathVariable Long id,
                                                                   @RequestBody RoleDTO roleDTO) {
        RoleResponseDTO updatedRole = roleService.updateRole(id, roleDTO);
        return ResponseHelper.ok("Role with ID: " + id + " updated successfully", updatedRole);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> patchRole(@PathVariable Long id,
                                                       @RequestBody RoleDTO roleDTO) {
        RoleResponseDTO updatedRole = roleService.patchRole(id, roleDTO);
        return ResponseHelper.ok("Role with ID: " + id + " patched successfully", updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseHelper.noContent();
    }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionEnum>>> getAllPermissions() {
        return ResponseHelper.ok("List of permissions fetched successfully", roleService.getAllPermissions());
    }
}
