package com.plus.service.user.application;

import com.plus.service.user.presentation.dto.TokenDto;

import java.util.UUID;

public interface TokenService {
    TokenDto createAccessToken(String userId, String userName);

    TokenDto createRefreshToken(String userId, String userName);

    UUID getUserIdFromAccessToken(String accessToken);

    UUID getUserIdFromRefreshToken(String refreshToken);

    boolean isValidAccessToken(String accessToken);

    boolean isValidRefreshToken(String refreshToken);
}
