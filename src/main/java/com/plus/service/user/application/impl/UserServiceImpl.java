package com.plus.service.user.application.impl;

import com.plus.service.global.error.BusinessException;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.application.UserService;
import com.plus.service.user.domain.User;
import com.plus.service.user.domain.repository.UserRepository;
import com.plus.service.user.error.TokenErrorCode;
import com.plus.service.user.error.UserErrorCode;
import com.plus.service.user.presentation.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMe(UserTokenDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BusinessException(TokenErrorCode.USER_TOKEN_INVALID));
        return UserResponse.of(user);
    }

    @Override
    public UserResponse signUp(SignUpRequest request) {
        if (userRepository.findByEmailAndDeletedIsFalse(request.email()).isPresent()) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .encodedPassword(passwordEncoder.encode(request.password()))
                .build();
        User save = userRepository.save(user);
        return UserResponse.of(save);
    }

    @Override
    public TokenResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmailAndDeletedIsFalse(request.email())
                .orElseThrow(() -> new BusinessException(UserErrorCode.LOGIN_FAILED));
        if(!passwordEncoder.matches(request.password(), user.getEncodedPassword())) {
            throw new BusinessException(UserErrorCode.LOGIN_FAILED);
        }
        TokenClaimDto tokenClaimDto = TokenClaimDto.of(user);
        TokenDto accessToken = tokenService.createAccessToken(tokenClaimDto);
        TokenDto refreshToken = tokenService.createRefreshToken(tokenClaimDto);
        return TokenResponse.of(accessToken, refreshToken);
    }



}
