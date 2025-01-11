package com.joyopi.backend.user.repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<UserEntity> findAll();

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    UserEntity save(UserEntity userEntity);

    void deleteById(Long id);
}
