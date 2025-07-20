package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface UserService {
    UserResponseDTO createUser(@RequestBody UserCreateDTO userCreateDTO);
}
