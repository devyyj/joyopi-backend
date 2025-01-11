package com.joyopi.backend.user.service;

import com.joyopi.backend.user.domain.User;

import java.util.List;

public interface UserService {

    /**
     * 새로운 사용자 생성
     * @param user 생성할 사용자 정보
     * @return 생성된 사용자
     */
    User createUser(User user);

    /**
     * ID로 사용자 조회
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 (Optional)
     */
    User getUserById(Long id);

    /**
     * 모든 사용자 조회
     * @return 사용자 목록
     */
    List<User> getAllUsers();

    /**
     * 사용자 정보 업데이트
     * @param id 업데이트할 사용자의 ID
     * @param user 업데이트할 사용자 정보
     * @return 업데이트된 사용자
     */
    User updateUser(Long id, User user);

    /**
     * 사용자 삭제
     * @param id 삭제할 사용자의 ID
     */
    void deleteUser(Long id);

    User createOrUpdateUser(String oauthProvider, String oauthId);
}