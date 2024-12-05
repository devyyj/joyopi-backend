package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.user.domain.User;
import com.example.springbootboilerplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthProvider = userRequest.getClientRegistration().getRegistrationId();
        String oauthId = oAuth2User.getName();
        User user = userService.createOrUpdateUser(oauthProvider, oauthId);

        // 권한 설정
        List<GrantedAuthority> authorities  = List.of(new SimpleGrantedAuthority(user.getRole()));

        // user id 설정
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", String.valueOf(user.getId()));

        // ClientRegistration에서 user-name-attribute 가져오기
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }
}