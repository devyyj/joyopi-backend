package com.example.springbootboilerplate.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}
