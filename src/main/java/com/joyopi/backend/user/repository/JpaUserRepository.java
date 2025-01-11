package com.joyopi.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}
