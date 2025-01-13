package com.joyopi.backend.freeboard.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FreeBoardPost {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // User 정보 (작성자)도 도메인 클래스에서 필요하다면 포함할 수 있습니다.
    private Long userId;
    private String userNickname;
}