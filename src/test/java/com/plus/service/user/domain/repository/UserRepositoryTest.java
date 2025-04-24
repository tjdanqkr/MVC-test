package com.plus.service.user.domain.repository;

import com.plus.service.user.domain.User;
import com.plus.service.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    void init(){
        for (int i = 0; i < 10; i++) {
            User user = User.builder()
                    .email("test"+i+"@test.com")
                    .encodedPassword("password")
                    .username("Test User"+i)
                    .deleted(i%2==0)
                    .build();
            userRepository.save(user);
        }
    }

    @Nested
    @DisplayName("이메일 + 삭제 여부 기반 사용자 조회 테스트")
    class FindByEmailAndDeletedIsFalse{
        @Test
        @DisplayName("존재하고 삭제되지 않은 이메일이면 User 반환")
        void shouldReturnUser_whenEmailExistsAndNotDeleted(){
            // Given
            String email = "test1@test.com";
            // When
            Optional<User> byEmailAndDeletedIsFalse = userRepository.findByEmailAndDeletedIsFalse(email);
            // Then
            assertTrue(byEmailAndDeletedIsFalse.isPresent());
            assertEquals(byEmailAndDeletedIsFalse.get().getEmail(), email);
        }
        @Test
        @DisplayName("없는 이메일이면 Null 반환")
        void shouldReturnEmpty_whenEmailDoesNotExist(){
            // Given
            String email = "test@test.com";
            // When
            Optional<User> byEmailAndDeletedIsFalse = userRepository.findByEmailAndDeletedIsFalse(email);
            // Then
            assertTrue(byEmailAndDeletedIsFalse.isEmpty());
        }
        @Test
        @DisplayName("삭제된 유저이면 Null 반환")
        void shouldReturnEmpty_whenUserIsDeleted(){
            // Given
            String email = "test0@test.com";
            // When
            Optional<User> byEmailAndDeletedIsFalse = userRepository.findByEmailAndDeletedIsFalse(email);
            // Then
            assertTrue(byEmailAndDeletedIsFalse.isEmpty());
        }

    }
}