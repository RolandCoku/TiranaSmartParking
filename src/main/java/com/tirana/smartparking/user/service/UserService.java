package com.tirana.smartparking.user.service;

import com.tirana.smartparking.auth.dto.UserLoginDTO;
import com.tirana.smartparking.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Service
public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO createUser(@RequestBody UserCreateDTO userCreateDTO);
    //    List<UserResponseDTO> getAllUsers();
    UserResponseDTO findById(Long id);
    UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    UserResponseDTO patchUser(Long id, UserUpdateDTO userUpdateDTO);
    void deleteUser(Long id);
    Page<UserCarsDTO> getUserCars(Long userId, Pageable pageable);
    UserCarsDTO addCarToUser(Long userId, CarCreateDTO carCreateDTO);
    void removeCarFromUser(Long userId, Long carId);
    UserResponseDTO addRoleToUser(Long userId, Set<String> roleName);
    UserResponseDTO removeRoleFromUser(Long userId, String roleName);
}
