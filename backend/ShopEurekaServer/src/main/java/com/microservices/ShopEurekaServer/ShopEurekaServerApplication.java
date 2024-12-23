package com.microservices.ShopEurekaServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication // Indica che questa è un'applicazione Spring Boot
@EnableEurekaServer // Abilita la funzionalità di Eureka Server per il servizio di registrazione
public class ShopEurekaServerApplication {

	public static void main(String[] args) {
		// Avvia l'applicazione Spring Boot e il server Eureka
		SpringApplication.run(ShopEurekaServerApplication.class, args);
	}

}
