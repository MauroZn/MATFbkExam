package com.microservice.ConfigurationClient.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TestController {

    @Value("${user.role:guest}")
    private String role;

    @Value("${user.password}")
    private String password;

    @GetMapping("/role")
    public String getRole() {
        return String.format("Hello! I am a %s and my password is %s ", role, password);
    }
}
