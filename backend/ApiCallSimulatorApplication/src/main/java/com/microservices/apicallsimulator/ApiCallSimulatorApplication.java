package com.microservices.apicallsimulator;

import com.microservices.apicallsimulator.services.ApiCallService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiCallSimulatorApplication {

    @Value("${catalog.url}")
    private String catalogUrl;

    @Value("${purchase.url}")
    private String purchaseUrl;

    public static void main(String[] args) {
        // SpringApplication.run(ApiCallSimulatorApplication.class, args); // Uncomment if you want to run Spring context
        SpringApplication.run(ApiCallSimulatorApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            // This code runs after the Spring context is fully initialized
            ApiCallService simulator = new ApiCallService(catalogUrl, purchaseUrl);
            simulator.startSimulation(1, TimeUnit.MINUTES);
        };
    }
}
