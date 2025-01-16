package com.joyopi.backend.user.service;

import com.joyopi.backend.user.domain.User;
import com.joyopi.backend.user.dto.UserRequestDto;
import com.joyopi.backend.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    /**
     * ID로 사용자 조회
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 (Optional)
     */
    UserResponseDto getUserById(Long id);

    /**
     * 사용자 정보 업데이트
     * @param id 업데이트할 사용자의 ID
     * @param UserRequestDto 업데이트할 사용자 정보
     * @return 업데이트된 사용자
     */
    UserResponseDto updateUser(Long id, UserRequestDto requestDto);

    /**
     * 사용자 삭제
     * @param id 삭제할 사용자의 ID
     */
    void deleteUser(Long id);


    /**
     * 사용자 존재 유무 확인하여 생성 또는 업데이트
     * @param oauthProvider
     * @param oauthId
     * @return
     */
    User createOrUpdateUser(String oauthProvider, String oauthId);
}