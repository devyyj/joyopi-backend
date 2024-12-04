package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.user.User;
import com.example.springbootboilerplate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String name = oAuth2User.getName();

        Optional<User> optionalUser = userRepository.findByOauthProviderAndOauthId(registrationId, name);

        List<GrantedAuthority> authorities;

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            userRepository.save(user);

            authorities = List.of(new SimpleGrantedAuthority(user.getRoles()));
        } else {
            User user = new User();
            user.setRoles("ROLE_USER");
            user.setOauthProvider(registrationId);
            user.setOauthId(name);
            user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            userRepository.save(user);

            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // ClientRegistration에서 user-name-attribute 가져오기
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 권한 설정

        // DefaultOAuth2User 반환
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), nameAttributeKey);
    }
}