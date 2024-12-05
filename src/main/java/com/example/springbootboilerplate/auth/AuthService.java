package com.example.springbootboilerplate.auth;

public interface AuthService {
    String reissueAccessToken(String refreshToken);
}
