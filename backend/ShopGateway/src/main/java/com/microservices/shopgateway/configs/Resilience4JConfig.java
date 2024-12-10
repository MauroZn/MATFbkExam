package com.microservices.shopgateway.configs;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration // Indica che questa è una classe di configurazione Spring
public class Resilience4JConfig {

    @Bean // Crea un bean per configurare il circuito di fallback
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        // Configurazione del Circuit Breaker
        var circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Se il 50% delle chiamate fallisce, il circuito si apre
                .waitDurationInOpenState(Duration.ofMillis(1000)) // Dopo quanto tempo il circuito si chiude per provare nuovamente
                .slidingWindowSize(2) // Numero di chiamate da considerare per determinare il tasso di errore
                .build();

        // Configurazione del Time Limiter (limite di tempo per le chiamate)
        var timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4)) // Imposta un timeout di 4 secondi per le chiamate
                .build();

        // Restituisce un Customizer per configurare il Circuit Breaker e il Time Limiter
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig) // Usa la configurazione del Circuit Breaker
                .timeLimiterConfig(timeLimiterConfig) // Usa la configurazione del Time Limiter
                .build());
    }
}
