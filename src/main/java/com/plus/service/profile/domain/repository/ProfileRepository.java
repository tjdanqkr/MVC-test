package com.plus.service.profile.domain.repository;

import com.plus.service.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    // TODO: 메소드 네임이 너무 길어서 QueryDSL로 바꿔야 함 또 분리 했으니 user 뺴야함
    List<Profile> findByUser_IdAndDeletedIsFalseAndUser_DeletedIsFalse(UUID userId);
}
