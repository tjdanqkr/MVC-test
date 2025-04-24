package com.plus.service.user.application.impl;

import com.plus.service.global.error.BusinessException;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.domain.User;
import com.plus.service.user.domain.repository.UserRepository;
import com.plus.service.user.error.TokenErrorCode;
import com.plus.service.user.presentation.dto.UserTokenDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
            BusinessException exception = assertThrows(BusinessException.class, () -> userDetailsService.loadUserByUsername(username));
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
            UserTokenDetails userDetails = (UserTokenDetails) userDetailsService.loadUserByUsername(username);

            // Then
            assertEquals(username, userDetails.getUsername());
        }
    }
}