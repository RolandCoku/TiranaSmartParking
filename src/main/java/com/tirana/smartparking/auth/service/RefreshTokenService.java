package com.tirana.smartparking.auth.service;

import com.tirana.smartparking.auth.dto.TokenResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface RefreshTokenService {
    TokenResponseDTO refreshAccessToken(String refreshToken);
    String generateRefreshToken(UserDetails userDetails);

}
