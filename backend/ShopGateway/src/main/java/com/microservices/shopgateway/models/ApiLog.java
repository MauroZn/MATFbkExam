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
    //Two types:
    //Searched (When the user asks the info of an item)
    //Sold (When an user buys an item)
    private String requestType;
    private int statusCode;
    private long responseTime;
    private final LocalDateTime timestamp;

    public ApiLog() {
        this.timestamp = LocalDateTime.now();
    }

}
