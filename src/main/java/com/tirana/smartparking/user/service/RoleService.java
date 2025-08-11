package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.dto.RoleResponseDTO;
import com.tirana.smartparking.user.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    RoleResponseDTO createRole(RoleDTO roleDTO);
    List<RoleResponseDTO> getRoles();
    RoleResponseDTO getRoleById(Long id);
    RoleResponseDTO updateRole(Long id, RoleDTO roleDTO);
    RoleResponseDTO patchRole(Long id, RoleDTO roleDTO);
    void deleteRole(Long id);
}
