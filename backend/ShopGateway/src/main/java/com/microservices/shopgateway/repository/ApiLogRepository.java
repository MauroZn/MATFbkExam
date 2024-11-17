package com.microservices.shopgateway.repository;

import com.microservices.shopgateway.models.ApiLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApiLogRepository extends MongoRepository<ApiLog, String> { }

