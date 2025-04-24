package com.plus.service.user.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.domain.User;
import com.plus.service.user.domain.repository.UserRepository;
import com.plus.service.user.presentation.dto.SignInRequest;
import com.plus.service.user.presentation.dto.SignUpRequest;
import com.plus.service.user.presentation.dto.TokenClaimDto;
import com.plus.service.user.presentation.dto.TokenDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private List<User> users;
    @BeforeEach
    void init(){
        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = User.builder()
                    .email("test"+i+"@test.com")
                    .encodedPassword(passwordEncoder.encode("password"))
                    .username("Test User"+i)
                    .deleted(i%2==0)
                    .build();
            users.add(user);
        }
        userRepository.saveAll(users);
    }


    @Nested
    @DisplayName("회원가입 테스트")
    class SignUp {
        @Test
        @DisplayName("성공")
        void signUp() throws Exception {
            // Given
            SignUpRequest signUpRequest = new SignUpRequest("test0@test.com", "username", "password");
            String body = objectMapper.writeValueAsString(signUpRequest);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/sign-up")
                            .content(body)
                            .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(201))
                    .andExpect(jsonPath("$.data.username").value("username"))
                    .andExpect(jsonPath("$.data.id").exists());
        }
        @Test
        @DisplayName("실패 - 이미 존재하는 이메일")
        void signUpEmailExists() throws Exception{
            // Given
            SignUpRequest signUpRequest = new SignUpRequest("test1@test.com", "username", "password");
            String body = objectMapper.writeValueAsString(signUpRequest);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/sign-up")
                            .content(body)
                            .contentType("application/json"))
                    .andExpect(status().isConflict()) // 409 Conflict
                    .andExpect(jsonPath("$.statusCode").value(409))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value("User already exists"));


        }
        @Test
        @DisplayName("실패 - 파라미터 문제")
        void signUpParameter() throws Exception{
            // Given
            SignUpRequest signUpRequest = new SignUpRequest("", "username", "password"); // 이메일이 null
            String body = objectMapper.writeValueAsString(signUpRequest);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/sign-up")
                            .content(body)
                            .contentType("application/json"))
                    .andExpect(status().isBadRequest()) // 400 Bad Request
                    .andExpect(jsonPath("$.statusCode").value(400))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value("Invalid input data"))
                    .andExpect(jsonPath("$.errors.size()").value(1))
                    .andExpect(jsonPath("$.errors[0].field").value("email"));
        }
    }
    @Nested
    @DisplayName("로그인 테스트")
    class SignIn {
        @Test
        @DisplayName("성공")
        void signIn() throws Exception{
            // Given
            SignInRequest signInRequest = new SignInRequest("test1@test.com", "password");
            String body = objectMapper.writeValueAsString(signInRequest);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/sign-in")
                            .content(body)
                            .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }
        @Test
        @DisplayName("실패 - 존재하지 않는 이메일")
        void signInEmailNotFound() throws Exception{
            // Given
            SignInRequest signInRequest = new SignInRequest("nonexistent@test.com", "password");
            String body = objectMapper.writeValueAsString(signInRequest);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/sign-in")
                            .content(body)
                            .contentType("application/json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").value(400))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value("Login failed"));
        }
        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void signInPasswordMismatch() throws Exception{
            // Given
            SignInRequest signInRequest = new SignInRequest("test1@test.com", "wrongPassword");
            String body = objectMapper.writeValueAsString(signInRequest);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/sign-in")
                            .content(body)
                            .contentType("application/json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").value(400))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value("Login failed"));
        }
    }
    @Nested
    @DisplayName("내 정보 조회 테스트")
    class GetMe {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void getMe() throws Exception {
            // Given
            User user = users.get(1);
            TokenDto accessToken = tokenService.createAccessToken(TokenClaimDto.of(user));

            // When & Then
            mockMvc.perform(get("/api/v1/auth/me")
                            .header("Authorization", "Bearer " + accessToken.token()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.data.username").value(user.getUsername()))
                    .andExpect(jsonPath("$.data.email").value(user.getEmail()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 유저")
        void getMeUserNotFound() throws Exception {
            // Given
            User user = users.get(0);
            TokenDto accessToken = tokenService.createAccessToken(TokenClaimDto.of(user));

            // When & Then
            mockMvc.perform(get("/api/v1/auth/me")
                            .header("Authorization", "Bearer " + accessToken.token()))
                    .andExpect(status().isUnauthorized());
        }
    }
    @Nested
    @DisplayName("토큰 리프레시 테스트")
    class RefreshToken {
        @Test
        @DisplayName("성공")
        void refresh() throws Exception {
            // Given
            User user = users.get(1);
            TokenDto accessToken = tokenService.createAccessToken(TokenClaimDto.of(user));
            TokenDto refreshToken = tokenService.createRefreshToken(TokenClaimDto.of(user));

            // When & Then
            mockMvc.perform(get("/api/v1/auth/refresh")
                            .header("Authorization", "Bearer " + accessToken.token())
                            .header("Refresh-Token", refreshToken.token()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }
        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void refreshUserNotFound() throws Exception {
            // Given
            User user = users.get(0);
            TokenDto accessToken = tokenService.createAccessToken(TokenClaimDto.of(user));
            TokenDto refreshToken = tokenService.createRefreshToken(TokenClaimDto.of(user));

            // When & Then
            mockMvc.perform(get("/api/v1/auth/refresh")
                            .header("Authorization", "Bearer " + accessToken.token())
                            .header("Refresh-Token", refreshToken.token()))
                    .andExpect(status().isUnauthorized());
        }
    }

}