package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    Role createRole(RoleDTO roleDTO);
    List<Role> getRoles();
    Role getRoleById(Long id);
}
