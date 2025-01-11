package com.joyopi.backend.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Configuration
public class WebClientConfig {

    @Value("${common.proxy.enabled}")
    private boolean proxyEnabled;

    @Value("${common.proxy.host}")
    private String proxyHost;

    @Value("${common.proxy.port}")
    private int proxyPort;

    @Bean
    public WebClient.Builder webClientBuilder() {
        // 프록시 설정이 필요 없으면 기본 WebClient 사용
        if (proxyEnabled) {
            // 프록시 설정
            HttpClient httpClient = HttpClient.create()
                    .proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP) // HTTP 프록시 사용
                            .host(proxyHost)  // 프록시 서버의 호스트
                            .port(proxyPort));  // 프록시 서버의 포트

            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient));
        } else {
            // 프록시 없이 WebClient 사용
            return WebClient.builder();
        }
    }
}
