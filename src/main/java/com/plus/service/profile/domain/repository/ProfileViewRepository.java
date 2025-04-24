package com.plus.service.profile.domain.repository;

import com.plus.service.profile.domain.ProfileView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileViewRepository extends JpaRepository<ProfileView, Long> {
    long countByProfileId(UUID profileId);
}
