package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
//    List<UserResponseDTO> getAllUsers();
    UserResponseDTO findById(Long id);
    UserResponseDTO createUser(@RequestBody UserCreateDTO userCreateDTO);
}
