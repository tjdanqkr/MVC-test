package com.plus.service.user.application;

import com.plus.service.user.presentation.dto.TokenClaimDto;
import com.plus.service.user.presentation.dto.TokenDto;



public interface TokenService {
    TokenDto createAccessToken(TokenClaimDto tokenClaimDto);

    TokenDto createRefreshToken(TokenClaimDto tokenClaimDto);

    TokenClaimDto getClaimsFromAccessToken(String accessToken);

    TokenClaimDto getClaimsFromRefreshToken(String refreshToken);

    boolean isValidAccessToken(String accessToken);

    boolean isValidRefreshToken(String refreshToken);
}
