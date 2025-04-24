package com.plus.service.user.presentation.dto;

import java.time.LocalDateTime;

public record TokenDto(
        String token,
        LocalDateTime expiresAt
) {
}
