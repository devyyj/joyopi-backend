package com.joyopi.backend.user.service;

import com.joyopi.backend.common.exception.CustomException;
import com.joyopi.backend.oauth.OAuthService;
import com.joyopi.backend.user.domain.User;
import com.joyopi.backend.user.repository.UserEntity;
import com.joyopi.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OAuthService oAuthService;

    @Override
    public User createUser(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDomain).toList();
    }

    @Override
    public User updateUser(Long id, User user) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        // 업데이트, 아직 할 정보가 없음

        return userMapper.toDomain(userRepository.save(entity));
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        oAuthService.unlink(userEntity.getOauthId(), userEntity.getOauthProvider());
        userRepository.deleteById(id);
    }

    @Override
    public User createOrUpdateUser(String oauthProvider, String oauthId) {
        UserEntity entity = userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElse(null);

        if (entity == null) {
            // 사용자 없으면 새로 생성
            entity = new UserEntity();
            entity.setRole("ROLE_USER");
            entity.setOauthProvider(oauthProvider);
            entity.setOauthId(oauthId);
        }

        entity.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        userRepository.save(entity);

        // UserEntity -> User 변환 후 반환
        return userMapper.toDomain(entity);
    }
}
