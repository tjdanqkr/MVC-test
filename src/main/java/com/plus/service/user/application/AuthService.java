package com.plus.service.user.application;

import com.plus.service.user.presentation.dto.UserTokenDetails;
import com.plus.service.user.presentation.dto.SignInRequest;
import com.plus.service.user.presentation.dto.SignUpRequest;
import com.plus.service.user.presentation.dto.TokenResponse;
import com.plus.service.user.presentation.dto.UserResponse;

public interface AuthService {
    UserResponse signUp(SignUpRequest request);

    TokenResponse signIn(SignInRequest request);

    UserResponse getMe(UserTokenDetails userDetails);
}
