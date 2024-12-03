package com.example.springbootboilerplate.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param seq 사용자 ID (Primary Key)
     * @return Optional로 래핑된 UserEntity
     */
    Optional<UserEntity> findBySeq(Long seq);

    /**
     * OAuth 제공자와 OAuth ID를 기반으로 사용자를 조회합니다.
     *
     * @param oauthProvider OAuth 제공자 (예: "kakao", "google")
     * @param oauthId OAuth 사용자 고유 ID
     * @return Optional로 래핑된 UserEntity
     */
    Optional<UserEntity> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    /**
     * 새로운 사용자를 저장합니다.
     *
     * @param userEntity 저장할 사용자 엔티티
     * @return 저장된 UserEntity
     */
    UserEntity save(UserEntity userEntity);

    /**
     * 모든 사용자를 조회합니다.
     *
     * @return 모든 사용자 리스트
     */
    List<UserEntity> findAll();

    /**
     * 사용자 삭제
     *
     * @param seq 삭제할 사용자 seq
     */
    void delete(Long seq);
}
