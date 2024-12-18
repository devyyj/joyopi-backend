package com.example.springbootboilerplate.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /*
        네트워크 프록시 사용해야 하는 경우 vm options
        -Dhttp.proxyHost=172.29.254.15
        -Dhttp.proxyPort=3128
        -Dhttps.proxyHost=172.29.254.15
        -Dhttps.proxyPort=3128

        http://localhost:8080/
        http://localhost:8080/h2-console
        http://localhost:8080/oauth2/authorization/kakao
        http://localhost:8080/role-user
     */

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService userService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/h2-console/**"
                                , "/"
                                , "/auth/**"
                                , "/oauth2/**"
                        ).permitAll() // 허용할 경로
                        .anyRequest().authenticated()) // 나머지 경로는 인증 필요
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)) // 401 반환, 이거 설정하면 기본 /login 페이지 동작하지 않음
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(userService))
                        .successHandler(successHandler)) // defaultSuccessUrl() 를 적용하면 successHandler()가 호출되지 않는 다는 사실!
                .logout(AbstractHttpConfigurer::disable) // /logout 기능 비활성화
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
        return http.build();
    }
}