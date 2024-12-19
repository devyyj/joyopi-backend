package com.example.springbootboilerplate.auth.service;

public interface AuthService {
    String reissueAccessToken(String refreshToken);
}
