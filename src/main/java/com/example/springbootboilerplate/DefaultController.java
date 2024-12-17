package com.example.springbootboilerplate;

import com.example.springbootboilerplate.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DefaultController {

    private final JwtUtil jwtUtil;

    @GetMapping
    public String root() {
        return "Hello World";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/role-user")
    public String roleUserOnly() {
        return "you have a user role";
    }

    @GetMapping("/role-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String roleAdminOnly(Authentication authentication) {
        if (authentication != null) {
            // 사용자의 권한 확인
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "Admin 권한 있음";
                }
            }
        }
        return "you have a admin role";
    }
}
