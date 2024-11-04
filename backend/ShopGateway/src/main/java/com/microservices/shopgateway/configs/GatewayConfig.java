package com.microservices.shopgateway.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("catalog", r -> r.path("/products/**")
                        .filters(f -> f
                                .circuitBreaker(cb ->
                                        cb.setFallbackUri("forward:/fallback-catalog"))
                                .stripPrefix(1))
                        .uri("lb://catalog"))

                .route("order", r -> r.path("/purchases/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://order"))
                .build();
    }
}
