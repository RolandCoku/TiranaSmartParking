package com.tirana.smartparking.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long accessTokenExpiresIn;
    private Long refreshTokenExpiresIn;
    private Long issuedAt;
}
