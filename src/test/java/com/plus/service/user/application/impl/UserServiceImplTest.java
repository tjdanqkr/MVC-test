package com.plus.service.user.application.impl;

import com.plus.service.global.dto.UserTokenDetails;
import com.plus.service.global.error.BusinessException;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.domain.User;
import com.plus.service.user.domain.repository.UserRepository;
import com.plus.service.user.error.TokenErrorCode;
import com.plus.service.user.error.UserErrorCode;
import com.plus.service.user.presentation.dto.SignInRequest;
import com.plus.service.user.presentation.dto.SignUpRequest;
import com.plus.service.user.presentation.dto.TokenDto;
import com.plus.service.user.presentation.dto.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTests {

        @Test
        @DisplayName("이미 존재하는 이메일로 실패")
        void signUpEmailExists() {
            // Given
            SignUpRequest request = new SignUpRequest("test@test.com", "username", "password");
            when(userRepository.findByEmailAndDeletedIsFalse(request.email())).thenReturn(Optional.of(User.builder().build()));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.signUp(request));
            assertEquals(UserErrorCode.USER_ALREADY_EXISTS.getMessage(), exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("회원가입 성공")
        void signUpSuccess() {
            // Given
            SignUpRequest request = new SignUpRequest("test@test.com", "username", "password");
            when(userRepository.findByEmailAndDeletedIsFalse(request.email())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

            // When
            userService.signUp(request);

            // Then
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class SignInTests {

        @Test
        @DisplayName("존재하지 않는 이메일로 실패")
        void signInEmailNotFound() {
            // Given
            SignInRequest request = new SignInRequest("test@test.com", "password");
            when(userRepository.findByEmailAndDeletedIsFalse(request.email())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.signIn(request));
            assertEquals(UserErrorCode.LOGIN_FAILED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호 불일치로 실패")
        void signInPasswordMismatch() {
            // Given
            SignInRequest request = new SignInRequest("test@test.com", "wrongPassword");
            User user = User.builder().email("test@test.com").encodedPassword("encodedPassword").build();
            when(userRepository.findByEmailAndDeletedIsFalse(request.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.password(), user.getEncodedPassword())).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.signIn(request));
            assertEquals(UserErrorCode.LOGIN_FAILED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("로그인 성공")
        void signInSuccess() {
            // Given
            SignInRequest request = new SignInRequest("test@test.com", "password");
            User user = User.builder().email("test@test.com").encodedPassword("encodedPassword").build();
            TokenDto accessToken = new TokenDto("accessToken", LocalDateTime.now().plusHours(2));
            TokenDto refreshToken = new TokenDto("refreshToken", LocalDateTime.now().plusHours(2));

            when(userRepository.findByEmailAndDeletedIsFalse(request.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.password(), user.getEncodedPassword())).thenReturn(true);
            when(tokenService.createAccessToken(any())).thenReturn(accessToken);
            when(tokenService.createRefreshToken(any())).thenReturn(refreshToken);

            // When
            TokenResponse response = userService.signIn(request);

            // Then
            assertEquals("accessToken", response.accessToken());
            assertEquals("refreshToken", response.refreshToken());
        }
    }

    @Nested
    @DisplayName("사용자 조회 테스트")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("존재하지 않는 사용자 이름으로 실패")
        void loadUserByUsernameNotFound() {
            // Given
            String username = "username";
            when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.loadUserByUsername(username));
            assertEquals(TokenErrorCode.USER_TOKEN_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("사용자 이름으로 조회 성공")
        void loadUserByUsernameSuccess() {
            // Given
            String username = "username";
            User user = User.builder().username(username).build();
            when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.of(user));

            // When
            UserTokenDetails userDetails = (UserTokenDetails) userService.loadUserByUsername(username);

            // Then
            assertEquals(username, userDetails.getUsername());
        }
    }
}