package com.plus.service.user.presentation.dto;

import java.time.LocalDateTime;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiresAt,
        LocalDateTime refreshTokenExpiresAt,
        String tokenType
) {
    private static final String baseTokenType = "Bearer";
    public static TokenResponse of(
            TokenDto accessToken,
            TokenDto refreshToken
    ) {
        return new TokenResponse(accessToken.token(), refreshToken.token(), accessToken.expiresAt(),refreshToken.expiresAt(), baseTokenType);
    }
}
