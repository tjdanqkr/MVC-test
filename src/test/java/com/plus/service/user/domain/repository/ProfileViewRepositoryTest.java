package com.plus.service.user.domain.repository;

import com.plus.service.user.domain.Profile;
import com.plus.service.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProfileViewRepositoryTest {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ProfileViewRepository profileViewRepository;
    User user1, user2;
    List<Profile> profiles;


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
        profiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Profile profile = Profile.builder()
                    .user(i%2==0?user1:user2)
                    .title("Test Profile"+i)
                    .deleted(i%3==0)
                    .description("Test Description"+i)
                    .profileImageUrl("https://test.com/test"+i+".jpg")
                    .address("Test Address"+i)
                    .build();
            profiles.add(profile);
        }
        profileRepository.saveAll(profiles);
        // user1: 5 profiles (3 not deleted)
        // user2: 5 profiles (2 not deleted) but deleted user
        for (int i = 0; i < 25; i++) {
            Profile profile = profiles.get(i / 10);
            profile.recordView();
            profileRepository.save(profile);
        }
        // profile0: 10 views
        // profile1: 10 views
        // profile2: 5 views
    }
    @Nested
    @DisplayName("사용자 ID + 삭제 여부 기반 프로필 조회 테스트")
    class CountByProfileId {
        @Test
        @DisplayName("존재하는 프로필 아이디로 조회했을떄 조회수 반환")
        void shouldReturnCount_whenExistProfileId() {
            // Given
            UUID profileId = profiles.get(0).getId();
            // When
            long count = profileViewRepository.countByProfileId(profileId);
            // Then
            assertEquals(10, count);
        }
    }
}