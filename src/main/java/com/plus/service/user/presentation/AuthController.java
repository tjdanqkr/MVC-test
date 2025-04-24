package com.plus.service.user.presentation;

import com.plus.service.global.dto.ApiResponse;
import com.plus.service.user.presentation.dto.UserTokenDetails;
import com.plus.service.user.application.AuthService;
import com.plus.service.user.presentation.dto.SignInRequest;
import com.plus.service.user.presentation.dto.SignUpRequest;
import com.plus.service.user.presentation.dto.TokenResponse;
import com.plus.service.user.presentation.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/api/v1/auth/sign-up")
    public ApiResponse<UserResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        UserResponse userResponse = authService.signUp(request);
        return ApiResponse.created(userResponse);
    }
    @PostMapping("/api/v1/auth/sign-in")
    public ApiResponse<TokenResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ApiResponse.success(authService.signIn(request));
    }
    @GetMapping("/api/v1/auth/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserTokenDetails user) {
        return ApiResponse.success(authService.getMe(user));
    }
    @GetMapping("/api/v1/auth/refresh")
    public ApiResponse<TokenResponse> refresh(
            @AuthenticationPrincipal UserTokenDetails user
        , @RequestHeader("Refresh-Token") String refreshToken) {
        return ApiResponse.success(authService.refresh(user, refreshToken));
    }

}
