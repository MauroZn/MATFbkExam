package com.microservices.shopgateway.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "logs")
public class ApiLog {

    @Id
    private String id;
    private String requestPath;
    private int statusCode;
    private long responseTime;
    private final LocalDateTime timestamp;

    public ApiLog() {
        this.timestamp = LocalDateTime.now();
    }

}
