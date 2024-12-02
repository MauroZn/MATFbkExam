package com.microservices.shopgateway.configs;

import com.microservices.shopgateway.models.ApiLog;
import com.microservices.shopgateway.repository.ApiLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log request details
        String requestPath = exchange.getRequest().getPath().toString();
        logger.info("Incoming request: {}", requestPath);

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // Log success case
                    long duration = System.currentTimeMillis() - startTime;
                    logger.info("Request completed for path: {} in {} ms", requestPath, duration);
                })
                .doOnError(error -> {
                    // Log error case
                    long duration = System.currentTimeMillis() - startTime;
                    logger.error("Error occurred on path: {} after {} ms, Error: {}", requestPath, duration, error.getMessage());
                })
                .doFinally(signalType -> {
                    // Cleanup or generic final logic if needed
                    logger.info("Processing finished for path: {}", requestPath);
                });
    }

    @Override
    public int getOrder() {
        return -1; // Ensures this runs early
    }
}*/

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Autowired
    private ApiLogRepository apiLogRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().toString();
        logger.info("Incoming request: {}", requestPath);

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).doOnSuccess(aVoid -> {
            long duration = System.currentTimeMillis() - startTime;

            // Save successful log to MongoDB
            ApiLog log = new ApiLog();
            log.setRequestType(getRequestType(requestPath));
            log.setStatusCode(exchange.getResponse().getStatusCode().value());
            log.setResponseTime(duration);

            apiLogRepository.save(log);
            logger.info("Request completed for path: {} in {} ms", requestPath, duration);
        }).doOnError(error -> {
            long duration = System.currentTimeMillis() - startTime;

            // Save error log to MongoDB
            ApiLog log = new ApiLog();
            log.setRequestType(getRequestType(requestPath));
            log.setStatusCode(500); // Assuming server error
            log.setResponseTime(duration);

            apiLogRepository.save(log);
            logger.error("Error occurred on path: {} after {} ms, Error: {}", requestPath, duration, error.getMessage());
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public String getRequestType(String requestPath) {

        if (requestPath.equals("/products/1701")) {
            return "Searched";
        }
        else if (requestPath.equals("/purchases/buy")) {
            return "Sold";
        }
        else {
            return "Unknown";
        }
    }
}

