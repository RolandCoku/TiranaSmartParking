package com.tirana.smartparking.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    String extractJti(String token);
    String hash(String jti);
    long getJwtExpiration();
    long getRefreshExpiration();
}
