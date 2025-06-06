package com.plus.service.user.application.impl;

import com.plus.service.global.error.BusinessException;
import com.plus.service.user.error.TokenErrorCode;
import com.plus.service.user.presentation.dto.TokenClaimDto;
import com.plus.service.user.presentation.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
// JWT 라이브러리 내부에서 clock 을 따로 사용하기 떄문에 Thread.sleep 이 최선임 clock 을 넣을 수 없음
class TokenServiceImplTest {
    TokenServiceImpl tokenService;
    long expiresIn = 1;
    String accessSecret = "secret";
    String refreshSecret = "refreshSecret";
    Clock clock = Clock.systemDefaultZone();
    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl(
                accessSecret,
                expiresIn,
                refreshSecret,
                expiresIn,
                clock
        );
    }

    @Test
    @DisplayName("AccessToken 생성 성공")
    void createAccessToken() {
        // Given
        UUID userId = UUID.randomUUID();
        String userName = "userName";
        LocalDateTime now = LocalDateTime.now();
        TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
        // When
        TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);

        // Then
        assertNotNull(tokenDto);
        assertNotNull(tokenDto.token());
        assertNotNull(tokenDto.expiresAt());
        assertTrue(tokenDto.expiresAt().isAfter(now));
    }

    @Test
    @DisplayName("RefreshToken 생성 성공")
    void createRefreshToken() {
        // Given
        UUID userId = UUID.randomUUID();
        String userName = "userName";
        TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
        LocalDateTime now = LocalDateTime.now();
        // When
        TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);

        // Then
        assertNotNull(tokenDto);
        assertNotNull(tokenDto.token());
        assertNotNull(tokenDto.expiresAt());
        assertTrue(tokenDto.expiresAt().isAfter(now));
    }

    @Nested
    @DisplayName("AccessToken 검증")
    class GetClaimsFromAccessToken{
        @Test
        @DisplayName("AccessToken 검증 성공")
        void getClaimsFromAccessToken_Success() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);
            // When
            TokenClaimDto claimsFromAccessToken = tokenService.getClaimsFromAccessToken(tokenDto.token());

            // Then
            assertEquals(tokenClaimDto, claimsFromAccessToken);
        }
        @Test
        @DisplayName("AccessToken 검증 실패 - 만료")
        void getClaimsFromAccessToken_Fail_Timeout() throws InterruptedException {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);
            Thread.sleep(expiresIn * 1000);
            // When & Then
            BusinessException businessException = assertThrows(BusinessException.class, () ->
                    tokenService.getClaimsFromAccessToken(tokenDto.token())
            );
            assertEquals(TokenErrorCode.USER_TOKEN_EXPIRED.getStatusCode(), businessException.getStatusCode());
            assertEquals(TokenErrorCode.USER_TOKEN_EXPIRED.getMessage(), businessException.getMessage());
        }
        @Test
        @DisplayName("AccessToken 검증 실패 - 서명 오류")
        void getClaimsFromAccessToken_Fail_Signature() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);
            // When & Then
            BusinessException businessException = assertThrows(BusinessException.class, () ->
                    tokenService.getClaimsFromAccessToken(tokenDto.token() + "123")
            );
            assertEquals(TokenErrorCode.USER_TOKEN_INVALID.getStatusCode(), businessException.getStatusCode());
            assertEquals(TokenErrorCode.USER_TOKEN_INVALID.getMessage(), businessException.getMessage());
        }
    }
    @Nested
    @DisplayName("RefreshToken 검증")
    class GetClaimsFromRefreshToken{
        @Test
        @DisplayName("RefreshToken 검증 성공")
        void getClaimsFromRefreshToken_Success() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);
            // When
            TokenClaimDto claimsFromRefreshToken = tokenService.getClaimsFromRefreshToken(tokenDto.token());

            // Then
            assertEquals(tokenClaimDto, claimsFromRefreshToken);
        }
        @Test
        @DisplayName("RefreshToken 검증 실패 - 만료")
        void getClaimsFromRefreshToken_Fail_Timeout() throws InterruptedException {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);
            Thread.sleep(expiresIn * 1000);
            // When & Then
            BusinessException businessException = assertThrows(BusinessException.class, () ->
                    tokenService.getClaimsFromRefreshToken(tokenDto.token())
            );
            assertEquals(TokenErrorCode.USER_TOKEN_EXPIRED.getStatusCode(), businessException.getStatusCode());
            assertEquals(TokenErrorCode.USER_TOKEN_EXPIRED.getMessage(), businessException.getMessage());
        }
        @Test
        @DisplayName("RefreshToken 검증 실패 - 서명 오류")
        void getClaimsFromRefreshToken_Fail_Signature() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);
            // When & Then
            BusinessException businessException = assertThrows(BusinessException.class, () ->
                    tokenService.getClaimsFromRefreshToken(tokenDto.token() + "123")
            );
            assertEquals(TokenErrorCode.USER_TOKEN_INVALID.getStatusCode(), businessException.getStatusCode());
            assertEquals(TokenErrorCode.USER_TOKEN_INVALID.getMessage(), businessException.getMessage());
        }
    }
    @Nested
    @DisplayName("AccessToken 검증")
    class IsValidAccessToken{
        @Test
        @DisplayName("AccessToken 검증 성공")
        void isValidAccessToken_Success() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);
            // When
            boolean validAccessToken = tokenService.isValidAccessToken(tokenDto.token());

            // Then
            assertTrue(validAccessToken);
        }
        @Test
        @DisplayName("AccessToken 검증 실패 - 만료")
        void isValidAccessToken_Fail_Timeout() throws InterruptedException {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);
            Thread.sleep(expiresIn * 1000);
            // When & Then
            assertFalse(tokenService.isValidAccessToken(tokenDto.token()));
        }
        @Test
        @DisplayName("AccessToken 검증 실패 - 서명 오류")
        void isValidAccessToken_Fail_Signature() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createAccessToken(tokenClaimDto);
            // When & Then
            assertFalse(tokenService.isValidAccessToken(tokenDto.token() + "123"));
        }
    }
    @Nested
    @DisplayName("RefreshToken 검증")
    class IsValidRefreshToken{
        @Test
        @DisplayName("RefreshToken 검증 성공")
        void isValidRefreshToken_Success() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);
            // When
            boolean validRefreshToken = tokenService.isValidRefreshToken(tokenDto.token());

            // Then
            assertTrue(validRefreshToken);
        }
        @Test
        @DisplayName("RefreshToken 검증 실패 - 만료")
        void isValidRefreshToken_Fail_Timeout() throws InterruptedException {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);
            Thread.sleep(expiresIn * 1000);
            // When & Then
            assertFalse(tokenService.isValidRefreshToken(tokenDto.token()));
        }
        @Test
        @DisplayName("RefreshToken 검증 실패 - 서명 오류")
        void isValidRefreshToken_Fail_Signature() {
            // Given
            UUID userId = UUID.randomUUID();
            String userName = "userName";
            TokenClaimDto tokenClaimDto = new TokenClaimDto(userId, userName);
            TokenDto tokenDto = tokenService.createRefreshToken(tokenClaimDto);
            // When & Then
            assertFalse(tokenService.isValidRefreshToken(tokenDto.token() + "123"));
        }
    }
}