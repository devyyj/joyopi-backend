package com.joyopi.backend.freeboard.dto;

import java.time.LocalDateTime;

public record FreeBoardPostResponseDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        String userNickname) {
}
