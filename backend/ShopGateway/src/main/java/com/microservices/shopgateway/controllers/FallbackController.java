package com.microservices.shopgateway.controllers;

import jakarta.ws.rs.GET;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping(value = "/fallback-catalog", produces = "application/json")
    public String fallbackCatalog() {
        return "{\"message\":\"We regret to inform service catalog is currently unavailable. please try again later\"}";
    }
}
