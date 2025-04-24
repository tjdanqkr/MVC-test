package com.plus.service.user.domain.repository;

import com.plus.service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndDeletedIsFalse(String email);
    Optional<User> findByUsernameAndDeletedIsFalse(String username);
}
