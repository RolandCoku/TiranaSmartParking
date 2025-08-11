package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.common.security.PermissionEnum;
import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.dto.RoleResponseDTO;
import com.tirana.smartparking.user.entity.Permission;
import com.tirana.smartparking.user.entity.Role;
import com.tirana.smartparking.user.repository.PermissionRepository;
import com.tirana.smartparking.user.repository.RoleRepository;
import com.tirana.smartparking.user.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<RoleResponseDTO> getRoles() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            throw new ResourceNotFoundException("No roles found");
        }

        return roles.stream()
                .map(this::mapToRoleResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO getRoleById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found"));

        return mapToRoleResponseDTO(role);
    }

    @Override
    public RoleResponseDTO createRole(RoleDTO roleDTO) {
        if (roleDTO == null || roleDTO.getName() == null || roleDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        // Check if a role with the same name already exists
        if (roleRepository.findByName(roleDTO.getName().trim().toUpperCase()).isPresent()) {
            throw new ResourceConflictException("Role with name " + roleDTO.getName() + " already exists");
        }

        Role role = new Role(
                roleDTO.getName().trim().toUpperCase(),
                roleDTO.getDescription()
        );

        // Add permissions
        for(String permissionName : roleDTO.getPermissions()) {
            role.addPermission(permissionRepository.findByName(PermissionEnum.valueOf(permissionName.trim().toUpperCase()))
                    .orElseThrow(() -> new ResourceNotFoundException("Permission " + permissionName + " not found")));
        }

        role = roleRepository.save(role);

        return mapToRoleResponseDTO(role);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found"));

        if (roleDTO == null || roleDTO.getName() == null || roleDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        // Check if a role with the same name already exists
        if (roleRepository.findByName(roleDTO.getName().trim().toUpperCase()).isPresent() &&
                !existingRole.getName().equals(roleDTO.getName().trim().toUpperCase())) {
            throw new ResourceConflictException("Role with name " + roleDTO.getName() + " already exists");
        }

        existingRole.setName(roleDTO.getName().trim().toUpperCase());
        existingRole.setDescription(roleDTO.getDescription());

        // Clear existing permissions and add new ones
        existingRole.getPermissions().clear();
        for(String permissionName : roleDTO.getPermissions()) {
            Permission permission = permissionRepository.findByName(PermissionEnum.valueOf(permissionName.trim().toUpperCase()))
                    .orElseThrow(() -> new ResourceNotFoundException("Permission " + permissionName + " not found"));
            existingRole.addPermission(permission);
        }

        Role role = roleRepository.save(existingRole);

        return mapToRoleResponseDTO(role);
    }

    @Override
    public RoleResponseDTO patchRole(Long id, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found"));

        if (roleDTO == null) {
            throw new IllegalArgumentException("Role DTO cannot be null");
        }

        // Update only the fields that are present in the DTO
        if (roleDTO.getName() != null && !roleDTO.getName().isEmpty()) {
            String newName = roleDTO.getName().trim().toUpperCase();
            // Check if a role with the same name already exists
            if (roleRepository.findByName(newName).isPresent() &&
                    !existingRole.getName().equals(newName)) {
                throw new ResourceConflictException("Role with name " + newName + " already exists");
            }
            existingRole.setName(newName);
        }
        if (roleDTO.getDescription() != null) {
            existingRole.setDescription(roleDTO.getDescription());
        }

        // Update permissions
        if (roleDTO.getPermissions() != null) {
            existingRole.getPermissions().clear();
            for(String permissionName : roleDTO.getPermissions()) {
                Permission permission = permissionRepository.findByName(PermissionEnum.valueOf(permissionName.trim().toUpperCase()))
                        .orElseThrow(() -> new ResourceNotFoundException("Permission " + permissionName + " not found"));
                existingRole.addPermission(permission);
            }
        }

        Role role = roleRepository.save(existingRole);
        return mapToRoleResponseDTO(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found"));

        // Check if the role is ADMIN or USER, which are not deletable
        if (role.getName().equalsIgnoreCase("ADMIN") || role.getName().equalsIgnoreCase("USER")) {
            throw new ResourceConflictException("Role " + role.getName() + " cannot be deleted");
        }

        // Check if the role exists in the database
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role with ID " + id + " does not exist");
        }

        // Check if any user is using the role
        if (roleRepository.isAssignedToUsers(role)) {
            throw new ResourceConflictException("Role with ID " + id + " cannot be deleted because it is assigned to users");
        }

        roleRepository.deleteById(id);
    }

    private RoleResponseDTO mapToRoleResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream()
                        .map(permission -> permission.getName().name())
                        .collect(Collectors.toSet())
        );
    }
}
