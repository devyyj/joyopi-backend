package com.joyopi.backend.auth.service;

public interface AuthService {
    String reissueAccessToken(String refreshToken);
}
