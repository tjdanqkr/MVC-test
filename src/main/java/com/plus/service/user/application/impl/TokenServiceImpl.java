package com.plus.service.user.application.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.plus.service.global.error.BusinessException;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.error.TokenErrorCode;
import com.plus.service.user.presentation.dto.TokenClaimDto;
import com.plus.service.user.presentation.dto.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    private final Algorithm accessTokenSecret;
    private final long accessTokenExpiresIn;
    private final Algorithm refreshTokenSecret;
    private final long refreshTokenExpiresIn;
    private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");
    private static final String TOKEN_USER_ID_KEY = "userId";


    @Override
    public TokenDto createAccessToken(TokenClaimDto tokenClaimDto) {
        Instant expiresAt = new Date(System.currentTimeMillis() + accessTokenExpiresIn)
                .toInstant()
                .atZone(zoneId)
                .toInstant();
        String token = JWT.create()
                .withSubject(tokenClaimDto.userName())
                .withClaim(TOKEN_USER_ID_KEY, tokenClaimDto.userId().toString())
                .withExpiresAt(expiresAt)
                .sign(accessTokenSecret);
        return new TokenDto(token, LocalDateTime.ofInstant(expiresAt, zoneId));
    }

    @Override
    public TokenDto createRefreshToken(TokenClaimDto tokenClaimDto) {
        Instant expiresAt = new Date(System.currentTimeMillis() + refreshTokenExpiresIn)
                .toInstant()
                .atZone(zoneId)
                .toInstant();
        String token = JWT.create()
                .withSubject(tokenClaimDto.userName())
                .withClaim(TOKEN_USER_ID_KEY, tokenClaimDto.userId().toString())
                .withExpiresAt(expiresAt)
                .sign(refreshTokenSecret);
        return new TokenDto(token, LocalDateTime.ofInstant(expiresAt, zoneId));
    }

    @Override
    public TokenClaimDto getClaimsFromAccessToken(String accessToken) {
        try {
            DecodedJWT verify = JWT.require(accessTokenSecret)
                    .build()
                    .verify(accessToken);
            String userName = verify.getSubject();
            String userId = verify
                    .getClaim(TOKEN_USER_ID_KEY)
                    .asString();
            return new TokenClaimDto(UUID.fromString(userId), userName);
        } catch (SignatureVerificationException e) {
            throw new BusinessException(TokenErrorCode.USER_TOKEN_INVALID);
        } catch (TokenExpiredException e) {
            throw new BusinessException(TokenErrorCode.USER_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(TokenErrorCode.USER_TOKEN_INVALID);
        }
    }

    @Override
    public TokenClaimDto getClaimsFromRefreshToken(String refreshToken) {
        try {
            DecodedJWT verify = JWT.require(refreshTokenSecret)
                    .build()
                    .verify(refreshToken);
            String userName = verify.getSubject();
            String userId = verify
                    .getClaim(TOKEN_USER_ID_KEY)
                    .asString();
            return new TokenClaimDto(UUID.fromString(userId), userName);
        } catch (SignatureVerificationException e) {
            throw new BusinessException(TokenErrorCode.USER_TOKEN_INVALID);
        } catch (TokenExpiredException e) {
            throw new BusinessException(TokenErrorCode.USER_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(TokenErrorCode.USER_TOKEN_INVALID);
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
