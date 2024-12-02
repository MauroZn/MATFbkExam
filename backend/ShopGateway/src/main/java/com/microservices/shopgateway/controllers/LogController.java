package com.microservices.shopgateway.controllers;

import com.microservices.shopgateway.models.ApiLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class LogController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/logs")
    public List<ApiLog> getLogs() {
        return mongoTemplate.findAll(ApiLog.class);
    }

}
