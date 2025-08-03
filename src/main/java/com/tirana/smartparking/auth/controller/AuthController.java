package com.tirana.smartparking.auth.controller;

import com.tirana.smartparking.auth.dto.RefreshRequestDTO;
import com.tirana.smartparking.auth.dto.TokenResponseDTO;
import com.tirana.smartparking.auth.dto.UserLoginDTO;
import com.tirana.smartparking.auth.service.AuthService;
import com.tirana.smartparking.auth.service.RefreshTokenService;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> login2(@RequestBody UserLoginDTO userLoginDTO) {
        TokenResponseDTO tokenResponseDTO = authService.login(userLoginDTO);
        return ResponseHelper.ok("Login successful", tokenResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> refresh(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        if (refreshRequestDTO.getRefreshToken() == null || refreshRequestDTO.getRefreshToken().isEmpty()) {
            return ResponseHelper.badRequest("Refresh token is required", null);
        }
        TokenResponseDTO tokenResponseDTO = refreshTokenService.refreshAccessToken(refreshRequestDTO.getRefreshToken());
        return ResponseHelper.ok("Access token refreshed successfully", tokenResponseDTO);
    }
}
