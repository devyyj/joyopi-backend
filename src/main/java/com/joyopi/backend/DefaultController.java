package com.joyopi.backend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DefaultController {

    @GetMapping
    public String root() {
        return "Hello World";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user-role-test")
    public String user() {
        return "you have a user role";
    }

    @GetMapping("/admin-role-test")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "you have a admin role";
    }
}
