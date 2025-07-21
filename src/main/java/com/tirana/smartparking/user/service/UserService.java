package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO createUser(@RequestBody UserCreateDTO userCreateDTO);
}
