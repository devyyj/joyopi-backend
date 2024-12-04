package com.example.springbootboilerplate.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    User save(User user);

    void deleteById(Long id);
}
