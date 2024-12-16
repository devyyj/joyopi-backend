package com.example.springbootboilerplate;

import com.example.springbootboilerplate.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/role-user")
    public String roleUserOnly() {
        return "you have a user role";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/role-admin")
    public String roleAdminOnly() {
        return "you have a admin role";
    }
}
