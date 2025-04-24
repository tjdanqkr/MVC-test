package com.plus.service.user.presentation.dto;

import com.plus.service.user.domain.User;
import java.util.UUID;


public record TokenClaimDto(
        UUID userId,
        String userName
){
    public static TokenClaimDto of(
            User user
    ) {
        return new TokenClaimDto(
                user.getId(),
                user.getUsername()
        );
    }

}
