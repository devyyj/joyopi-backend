package com.example.springbootboilerplate.user.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String oauthProvider;
    private String oauthId;
    private String role;
    private LocalDateTime lastLogin;
}
