package com.tirana.smartparking.auth.controller;

import com.tirana.smartparking.auth.dto.RefreshRequestDTO;
import com.tirana.smartparking.auth.dto.TokenResponseDTO;
import com.tirana.smartparking.auth.dto.UserLoginDTO;
import com.tirana.smartparking.auth.dto.UserRegistrationDTO;
import com.tirana.smartparking.auth.service.AuthService;
import com.tirana.smartparking.auth.service.RefreshTokenService;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, UserService userService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> login2(@RequestBody UserLoginDTO userLoginDTO) {
        TokenResponseDTO tokenResponseDTO = authService.login(userLoginDTO);
        return ResponseHelper.ok("Login successful", tokenResponseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (userRegistrationDTO.getUsername() == null || userRegistrationDTO.getPassword() == null ||
        userRegistrationDTO.getUsername().isEmpty() || userRegistrationDTO.getPassword().isEmpty() ||
        userRegistrationDTO.getEmail() == null || userRegistrationDTO.getEmail().isEmpty()) {
            return ResponseHelper.badRequest("Username and password are required", null);
        }

        UserCreateDTO userCreateDTO = new UserCreateDTO(
                userRegistrationDTO.getFirstName(),
                userRegistrationDTO.getLastName(),
                userRegistrationDTO.getUsername(),
                userRegistrationDTO.getPassword(),
                userRegistrationDTO.getConfirmPassword(),
                userRegistrationDTO.getEmail(),
                userRegistrationDTO.getPhoneNumber(),
                Set.of("USER") // Default role for new users
        );

        UserResponseDTO userResponseDTO = userService.createUser(userCreateDTO);

        return ResponseHelper.ok("Registration successful", userResponseDTO);
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
