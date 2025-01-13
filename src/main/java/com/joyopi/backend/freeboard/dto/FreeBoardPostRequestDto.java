package com.joyopi.backend.freeboard.dto;

import lombok.Data;

@Data
public class FreeBoardPostRequestDto {
    private String title;
    private String content;
    private Long userId;
}
