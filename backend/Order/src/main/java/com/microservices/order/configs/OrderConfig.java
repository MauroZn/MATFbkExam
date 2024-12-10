package com.microservices.order.configs;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Indica che questa classe è una configurazione di Spring
public class OrderConfig {

    @Bean // Definisce un bean che sarà gestito dal contesto di Spring
    @LoadBalanced // Abilita il bilanciamento del carico lato client per le chiamate REST
    RestTemplate restTemplate() {
        // Restituisce un'istanza di RestTemplate per eseguire chiamate HTTP
        return new RestTemplate();
    }

}
