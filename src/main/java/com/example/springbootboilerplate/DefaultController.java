package com.example.springbootboilerplate;

import com.example.springbootboilerplate.common.security.CustomOAuth2User;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DefaultController implements ErrorController {

    @RequestMapping
    public String root(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return oAuth2User.toString();
    }

    @RequestMapping("/error")
    public String error(){
        return "error";
    }
}
