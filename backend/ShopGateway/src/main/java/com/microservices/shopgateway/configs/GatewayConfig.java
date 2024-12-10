package com.microservices.shopgateway.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Indica che questa classe fornisce configurazioni di Spring
public class GatewayConfig {

    @Bean // Definisce un bean per configurare le rotte del gateway
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Configurazione della rotta per il servizio "catalog"
                .route("catalog", r -> r.path("/products/**") // Matcha tutte le richieste che iniziano con "/products/"
                        .filters(f -> f
                                .circuitBreaker(cb -> cb.setFallbackUri("forward:/fallback-catalog")) // Configura il Circuit Breaker con un URI di fallback
                                .stripPrefix(1)) // Rimuove il primo segmento di path prima di inoltrare la richiesta
                        .uri("lb://catalog")) // Indica che la richiesta verrà inoltrata al servizio "catalog" tramite il bilanciamento del carico

                // Configurazione della rotta per il servizio "order"
                .route("order", r -> r.path("/purchases/**") // Matcha tutte le richieste che iniziano con "/purchases/"
                        .filters(f -> f.stripPrefix(1)) // Rimuove il primo segmento di path prima di inoltrare la richiesta
                        .uri("lb://order")) // Indica che la richiesta verrà inoltrata al servizio "order" tramite il bilanciamento del carico
                .build(); // Costruisce e restituisce il RouteLocator
    }
}
