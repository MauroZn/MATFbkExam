package com.microservices.order.configs;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration // Indica che questa classe è una configurazione di Spring
public class Resilience4JConfig {

    @Bean // Definisce un bean per configurare il circuito di resilienza
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        // Configurazione del Circuit Breaker
        var circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Soglia di errore: 50% delle richieste devono fallire per aprire il circuito
                .waitDurationInOpenState(Duration.ofMillis(1000)) // Durata dello stato aperto: 1 secondo
                .slidingWindowSize(2) // Dimensione della finestra di scorrimento: 2 richieste
                .build();

        // Configurazione del Time Limiter
        var timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4)) // Timeout massimo per una richiesta: 4 secondi
                .build();

        // Configurazione di default per tutti i circuit breaker
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig) // Applica la configurazione del Circuit Breaker
                .timeLimiterConfig(timeLimiterConfig) // Applica la configurazione del Time Limiter
                .build());
    }
}
