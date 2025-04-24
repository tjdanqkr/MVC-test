package com.plus.service.user.application.impl;

import com.plus.service.user.presentation.dto.UserTokenDetails;
import com.plus.service.global.error.BusinessException;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.domain.User;
import com.plus.service.user.domain.repository.UserRepository;
import com.plus.service.user.error.TokenErrorCode;
import com.plus.service.user.error.UserErrorCode;
import com.plus.service.user.presentation.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
            when(userRepository.save(any(User.class))).thenReturn(User.builder()
                    .id(UUID.randomUUID())
                    .email("test@test.com")
                    .username("username")
                    .build());
            // When
            UserResponse userResponse = userService.signUp(request);

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
    @DisplayName("getMe 테스트")
    class GetMeTests {
        @Test
        @DisplayName("getMe 테스트")
        void getMe() {
            // Given
            User user = User.builder().id(UUID.randomUUID()).username("testUser").build();
            UserTokenDetails userDetails = UserTokenDetails.of(user);
            when(userRepository.findById(userDetails.getId()))
                    .thenReturn(Optional.of(user));

            // When
            UserResponse response = userService.getMe(userDetails);

            // Then
            assertEquals(user.getUsername(), response.username());
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 실패")
        void getMeUserNotFound() {
            // Given
            UserTokenDetails userDetails = UserTokenDetails.of(User.builder().id(UUID.randomUUID()).build());
            when(userRepository.findById(userDetails.getId())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.getMe(userDetails));
            assertEquals(TokenErrorCode.USER_TOKEN_INVALID.getMessage(), exception.getMessage());
        }
    }

}