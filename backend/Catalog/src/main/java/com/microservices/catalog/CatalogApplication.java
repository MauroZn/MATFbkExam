package com.microservices.catalog;

import brave.Tracing;
import brave.mongodb.MongoDBTracing;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.event.CommandListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableDiscoveryClient
public class CatalogApplication {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}


	@Bean
	@Profile("!test")
	public MongoClient getMongoClient() {
		CommandListener listener = MongoDBTracing.create(Tracing.current())
				.commandListener();

		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(mongoUri))
				.addCommandListener(listener).build();
		return MongoClients.create(settings);
	}
}
