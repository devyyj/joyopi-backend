package com.example.springbootboilerplate.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    @Value("${kakao.admin-key}")
    private String adminKey;

    @Value("${kakao.unlink-uri}")
    private String unlinkUri;

    private final WebClient.Builder webClientBuilder;

    @Override
    public void unlink(String id, String provider) {
        if(provider != null && provider.equals("kakao")) {
            WebClient webClient = webClientBuilder.baseUrl(unlinkUri).build();

            // "target_id_type=user_id&target_id=123456789" 형태로 요청 데이터 설정
            String requestBody = "target_id_type=user_id&target_id=" + id;

            // Kakao API에 unlink 요청
            ResponseEntity<String> response = webClient
                    .post()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .header("Authorization", "KakaoAK " + adminKey)  // Kakao API 인증 헤더
                    .body(BodyInserters.fromValue(requestBody))  // URL 인코딩된 본문 데이터 설정
                    .retrieve()
                    .toEntity(String.class)  // 응답을 문자열로 처리
                    .block();// 블로킹 방식으로 결과를 기다림

            System.out.println("response = " + response);
        }
    }
}
