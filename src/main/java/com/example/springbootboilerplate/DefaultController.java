package com.example.springbootboilerplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Slf4j
public class DefaultController implements ErrorController {

    @RequestMapping("/success")
    public String root(){
        return "Hello World";
    }

    @RequestMapping("/error")
    public String error(){
        return "error";
    }
}
