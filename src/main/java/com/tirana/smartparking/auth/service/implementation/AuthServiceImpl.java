package com.tirana.smartparking.auth.service.implementation;

import com.tirana.smartparking.auth.dto.TokenResponseDTO;
import com.tirana.smartparking.auth.dto.UserLoginDTO;
import com.tirana.smartparking.auth.service.AuthService;
import com.tirana.smartparking.auth.service.JwtService;
import com.tirana.smartparking.auth.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public TokenResponseDTO login(UserLoginDTO userLoginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = refreshTokenService.generateRefreshToken(userDetails);

            return new TokenResponseDTO(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtService.getJwtExpiration(),
                    jwtService.getRefreshExpiration(),
                    System.currentTimeMillis()
            );
        } catch (Exception e) {
            // Handle authentication failure
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
