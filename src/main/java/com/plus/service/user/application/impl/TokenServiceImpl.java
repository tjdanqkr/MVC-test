package com.plus.service.user.application.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.error.TokenError;
import com.plus.service.user.presentation.dto.TokenDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final Algorithm accessTokenSecret;
    private final long accessTokenExpiresIn;
    private final Algorithm refreshTokenSecret;
    private final long refreshTokenExpiresIn;
    private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");
    private static final String TOKEN_USER_ID_KEY = "userId";


    @Override
    public TokenDto createAccessToken(String userId, String userName) {
        Instant expiresAt = new Date(System.currentTimeMillis() + accessTokenExpiresIn)
                .toInstant()
                .atZone(zoneId)
                .toInstant();
        String token = JWT.create()
                .withSubject(userName)
                .withClaim(TOKEN_USER_ID_KEY, userId)
                .withExpiresAt(expiresAt)
                .sign(accessTokenSecret);
        return new TokenDto(token, LocalDateTime.ofInstant(expiresAt, zoneId));
    }

    @Override
    public TokenDto createRefreshToken(String userId, String userName) {
        Instant expiresAt = new Date(System.currentTimeMillis() + refreshTokenExpiresIn)
                .toInstant()
                .atZone(zoneId)
                .toInstant();
        String token = JWT.create()
                .withSubject(userName)
                .withClaim(TOKEN_USER_ID_KEY, userId)
                .withExpiresAt(expiresAt)
                .sign(refreshTokenSecret);
        return new TokenDto(token, LocalDateTime.ofInstant(expiresAt, zoneId));
    }

    @Override
    public UUID getUserIdFromAccessToken(String accessToken) {
        try {
            String userId = JWT.require(accessTokenSecret)
                    .build()
                    .verify(accessToken)
                    .getClaim(TOKEN_USER_ID_KEY)
                    .asString();
            return UUID.fromString(userId);
        } catch (Exception e) {
            throw new TokenError();
        }
    }

    @Override
    public UUID getUserIdFromRefreshToken(String refreshToken) {
        try {
            String userId = JWT.require(refreshTokenSecret)
                    .build()
                    .verify(refreshToken)
                    .getClaim(TOKEN_USER_ID_KEY)
                    .asString();
            return UUID.fromString(userId);
        } catch (Exception e) {
            throw new TokenError();
        }
    }

    @Override
    public boolean isValidAccessToken(String accessToken) {
        try {
            JWT.require(accessTokenSecret)
                    .build()
                    .verify(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            JWT.require(refreshTokenSecret)
                    .build()
                    .verify(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public TokenServiceImpl(@Value("${token.access.secret}") String accessTokenSecret,
                            @Value("${token.access.expiration}") long accessTokenExpiresIn,
                            @Value("${token.refresh.secret}") String refreshTokenSecret,
                            @Value("${token.refresh.expiration}") long refreshTokenExpiresIn) {
        this.accessTokenSecret = Algorithm.HMAC256(accessTokenSecret);
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenSecret = Algorithm.HMAC256(refreshTokenSecret);
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}
