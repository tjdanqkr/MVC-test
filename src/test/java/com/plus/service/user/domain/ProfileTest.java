package com.plus.service.user.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    void recordView() {
        // Given
        Profile profile = Profile.builder().build();
        // When
        profile.recordView();
        // Then
        assertEquals(1, profile.getProfileViews().size());
    }

    @Test
    void deleted() {
        // Given
        Profile profile = Profile.builder().deleted(false).build();
        // When
        profile.deleted();
        // Then
        assertTrue(profile.isDeleted());
    }
}