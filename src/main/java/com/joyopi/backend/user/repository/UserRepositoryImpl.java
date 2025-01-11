package com.joyopi.backend.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;

    @Override
    public List<UserEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByOauthProviderAndOauthId(String oauthProvider, String oauthId) {
        return jpaRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return jpaRepository.save(userEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
