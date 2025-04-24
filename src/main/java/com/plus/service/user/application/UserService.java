package com.plus.service.user.application;

import com.plus.service.user.presentation.dto.SignInRequest;
import com.plus.service.user.presentation.dto.SignUpRequest;
import com.plus.service.user.presentation.dto.TokenResponse;

public interface UserService {
    void signUp(SignUpRequest request);

    TokenResponse signIn(SignInRequest request);
}
