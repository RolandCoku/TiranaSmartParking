package com.tirana.smartparking.auth.service;

import com.tirana.smartparking.auth.dto.TokenResponseDTO;
import com.tirana.smartparking.auth.dto.UserLoginDTO;

public interface AuthService {
    TokenResponseDTO login(UserLoginDTO userLoginDTO);
}
