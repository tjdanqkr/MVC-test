package com.plus.service.user.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void deleted() {
        // Given
        User user = User.builder().deleted(false).build();
        // When
        user.deleted();
        // Then
        assertTrue(user.isDeleted());
    }
}