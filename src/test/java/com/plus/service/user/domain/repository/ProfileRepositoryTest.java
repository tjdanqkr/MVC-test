package com.plus.service.user.domain.repository;

import com.plus.service.user.domain.Profile;
import com.plus.service.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProfileRepositoryTest {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    User user1, user2;
    @BeforeEach
    void init(){
        user1 = User.builder()
                .email("test@test.com")
                .encodedPassword("password")
                .username("Test User")
                .deleted(false)
                .build();
        userRepository.save(user1);
        user2 = User.builder()
                .email("test1@test.com")
                .encodedPassword("password")
                .username("Test User1")
                .deleted(true)
                .build();
        userRepository.save(user2);
        for (int i = 0; i < 10; i++) {
            Profile profile = Profile.builder()
                    .user(i%2==0?user1:user2)
                    .title("Test Profile"+i)
                    .deleted(i%3==0)
                    .description("Test Description"+i)
                    .profileImageUrl("https://test.com/test"+i+".jpg")
                    .address("Test Address"+i)
                    .build();
            profileRepository.save(profile);
        }
        // user1: 5 profiles (3 not deleted)
        // user2: 5 profiles (2 not deleted) but deleted user
    }

    @Nested
    @DisplayName("사용자 ID + 삭제 여부 기반 프로필 조회 테스트")
    class FindByUser_IdAndDeletedIsFalseAndUser_DeletedIsFalse {
        @Test
        @DisplayName("존재하고 삭제되지 않은 사용자 ID이면 프로필 리스트 반환")
        void shouldReturnListSize5_whenNotDeletedUserAndNotDeleted(){
            // Given
            UUID userId = user1.getId();
            // When
            List<Profile> profiles = profileRepository.findByUser_IdAndDeletedIsFalseAndUser_DeletedIsFalse(userId);
            // Then
            assertEquals(3, profiles.size());
        }
        @Test
        @DisplayName("삭제된 사용자 ID이면 빈 리스트 반환")
        void shouldReturnListSize0_whenDeletedUserAndNotDeleted(){
            // Given
            UUID userId = user2.getId();
            // When
            List<Profile> profiles = profileRepository.findByUser_IdAndDeletedIsFalseAndUser_DeletedIsFalse(userId);
            // Then
            assertEquals(0, profiles.size());
        }
        @Test
        @DisplayName("없는 사용자 ID이면 빈 리스트 반환")
        void shouldReturnListSize0_whenNotFoundUserId(){
            // Given
            UUID userId = UUID.randomUUID();
            // When
            List<Profile> profiles = profileRepository.findByUser_IdAndDeletedIsFalseAndUser_DeletedIsFalse(userId);
            // Then
            assertEquals(0, profiles.size());
        }

    }
}