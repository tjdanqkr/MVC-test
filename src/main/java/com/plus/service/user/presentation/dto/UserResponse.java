package com.plus.service.user.presentation.dto;


import com.plus.service.user.domain.User;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String username,
        String email,
        LocalDateTime createdAt
) {
    public static UserResponse of(User user){
        return new UserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
