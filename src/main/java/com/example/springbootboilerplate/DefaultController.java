package com.example.springbootboilerplate;

import com.example.springbootboilerplate.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
}
