package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.entity.Role;
import com.tirana.smartparking.user.repository.RoleRepository;
import com.tirana.smartparking.user.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found"));
    }

    @Override
    public Role createRole(RoleDTO roleDTO) {
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

        role = roleRepository.save(role);

        return role;
    }


}
