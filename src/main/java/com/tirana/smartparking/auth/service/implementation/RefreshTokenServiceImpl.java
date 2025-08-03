package com.tirana.smartparking.auth.service.implementation;

import com.tirana.smartparking.auth.dto.TokenResponseDTO;
import com.tirana.smartparking.auth.entity.RefreshToken;
import com.tirana.smartparking.auth.repository.RefreshTokenRepository;
import com.tirana.smartparking.auth.service.RefreshTokenService;
import com.tirana.smartparking.common.exception.InvalidRefreshTokenException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtServiceImpl jwtService;
    private final UserDetailsService userDetailsService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtServiceImpl jwtService, UserDetailsService userDetailsService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public String generateRefreshToken(UserDetails userDetails) {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        String username = userDetails.getUsername();
        String jti = jwtService.extractJti(refreshToken);
        String jtiHash = jwtService.hash(jti);

        RefreshToken storedRefreshToken = new RefreshToken();
        storedRefreshToken.setUsername(username);
        storedRefreshToken.setTokenIdHash(jtiHash);
        storedRefreshToken.setIssuedAt(new Date());
        storedRefreshToken.setExpiresAt(new Date(System.currentTimeMillis() + jwtService.getRefreshExpiration()));
        storedRefreshToken.setRevoked(false);

        refreshTokenRepository.save(storedRefreshToken);

        return refreshToken;
    }

    @Override
    public TokenResponseDTO refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        String jti = jwtService.extractJti(refreshToken);
        String jtiHash = jwtService.hash(jti);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByTokenIdHash(jtiHash)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if(storedRefreshToken.isRevoked() || !jwtService.isTokenValid(refreshToken, userDetails)) {
            //TODO Later: Add reuse detection logic to prevent reuse of refresh tokens
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        storedRefreshToken.setRevoked(true);
        refreshTokenRepository.save(storedRefreshToken);

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        RefreshToken newStoredRefreshToken = new RefreshToken();
        newStoredRefreshToken.setUsername(username);
        newStoredRefreshToken.setTokenIdHash(jwtService.hash(newRefreshToken));
        newStoredRefreshToken.setIssuedAt(new Date());
        newStoredRefreshToken.setExpiresAt(new Date(System.currentTimeMillis() + jwtService.getRefreshExpiration()));
        newStoredRefreshToken.setRevoked(false);
        refreshTokenRepository.save(newStoredRefreshToken);

        return new TokenResponseDTO(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getJwtExpiration(),
                jwtService.getRefreshExpiration(),
                System.currentTimeMillis()
        );
    }
}
