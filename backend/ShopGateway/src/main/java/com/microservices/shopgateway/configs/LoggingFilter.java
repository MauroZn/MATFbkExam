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

@Component // Indica che questa è una classe Spring che può essere gestita dal contenitore di Spring
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class); // Logger per registrare informazioni

    @Autowired
    private ApiLogRepository apiLogRepository; // Repository per salvare i log delle API

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Ottieni il percorso della richiesta in arrivo
        String requestPath = exchange.getRequest().getPath().toString();
        logger.info("Incoming request: {}", requestPath); // Logga l'arrivo di una richiesta

        long startTime = System.currentTimeMillis(); // Memorizza il tempo di inizio per calcolare la durata della richiesta

        // Esegui il filtro e registra il tempo di risposta
        return chain.filter(exchange).doOnSuccess(aVoid -> {
            long duration = System.currentTimeMillis() - startTime; // Calcola la durata della richiesta

            // Crea un oggetto ApiLog per registrare le informazioni sul log
            ApiLog log = new ApiLog();
            log.setRequestType(getRequestType(requestPath)); // Imposta il tipo di richiesta in base al percorso
            log.setStatusCode(exchange.getResponse().getStatusCode().value()); // Imposta il codice di stato della risposta
            log.setResponseTime(duration); // Imposta il tempo di risposta

            // Salva il log nel repository MongoDB
            apiLogRepository.save(log);
            logger.info("Request completed for path: {} in {} ms", requestPath, duration); // Logga il completamento della richiesta
        }).doOnError(error -> {
            long duration = System.currentTimeMillis() - startTime; // Calcola la durata in caso di errore

            // Crea un oggetto ApiLog per registrare il log dell'errore
            ApiLog log = new ApiLog();
            log.setRequestType(getRequestType(requestPath)); // Imposta il tipo di richiesta in base al percorso
            log.setStatusCode(500); // Imposta il codice di stato a 500 per errore del server
            log.setResponseTime(duration); // Imposta il tempo di risposta

            // Salva il log dell'errore nel repository MongoDB
            apiLogRepository.save(log);
            logger.error("Error occurred on path: {} after {} ms, Error: {}", requestPath, duration, error.getMessage()); // Logga l'errore
        });
    }

    @Override
    public int getOrder() {
        return -1; // Imposta l'ordine del filtro, con un valore negativo per eseguirlo prima di altri filtri
    }

    // Metodo che determina il tipo di richiesta in base al percorso
    public String getRequestType(String requestPath) {

        if (requestPath.equals("/products/1701")) {
            return "Searched"; // Se il percorso è /products/1701, viene classificata come richiesta "Searched"
        }
        else if (requestPath.equals("/purchases/buy")) {
            return "Sold"; // Se il percorso è /purchases/buy, viene classificata come richiesta "Sold"
        }
        else {
            return "Unknown"; // Altri percorsi vengono classificati come "Unknown"
        }
    }
}
