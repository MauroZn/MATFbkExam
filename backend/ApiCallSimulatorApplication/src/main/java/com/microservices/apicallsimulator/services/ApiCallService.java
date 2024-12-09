package com.microservices.apicallsimulator.services;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.microservices.order.models.dto.OrderRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class ApiCallService {

    private final String catalogUrl; // URL del servizio di catalogo
    private final String purchaseUrl; // URL del servizio di acquisti
    private final RestTemplate restTemplate; // Client HTTP per inviare richieste
    private final ScheduledExecutorService scheduler; // Esecutore per pianificare attività
    private final Random random; // Generatore di numeri casuali per intervalli

    // Costruttore per inizializzare le proprietà
    public ApiCallService(String catalogUrl, String purchaseUrl) {
        this.catalogUrl = catalogUrl; // Assegna l'URL del catalogo
        this.purchaseUrl = purchaseUrl; // Assegna l'URL degli acquisti
        this.restTemplate = new RestTemplate(); // Inizializza RestTemplate
        this.scheduler = Executors.newScheduledThreadPool(2); // Inizializza un esecutore con 2 thread
        this.random = new Random(); // Inizializza il generatore casuale
    }

    // Metodo per generare un intervallo casuale tra 15 e 30 secondi
    private int randomInterval() {
        return 15 + random.nextInt(30); // Aggiunge un numero casuale (0-29) a 15
    }

    // Simula una ricerca nel catalogo
    private void simulateCatalogSearch() {
        try {
            String searchUrl = catalogUrl + "/1701"; // Costruisce l'URL per cercare un prodotto con ID 1701
            ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class); // Esegue una richiesta GET
            System.out.println("Catalog Search Response: " + response.getStatusCode()); // Log dello stato della risposta
        } catch (Exception e) {
            System.err.println("Error in Catalog Search API Call: " + e.getMessage()); // Log degli errori
        }
    }

    // Simula un acquisto
    private void simulatePurchase() {
        try {
            OrderRequest orderRequest = new OrderRequest(); // Crea una richiesta d'ordine
            orderRequest.setUserId("1234"); // Imposta l'ID utente
            orderRequest.setCount(1); // Imposta la quantità
            orderRequest.setProductId("1701"); // Imposta l'ID del prodotto

            // Crea un'entità HTTP con intestazioni e corpo della richiesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Imposta il tipo di contenuto come JSON
            HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);

            // Esegue una richiesta POST per l'acquisto
            ResponseEntity<String> response = restTemplate.exchange(
                    purchaseUrl + "/buy", // Endpoint per l'acquisto
                    HttpMethod.POST, // Metodo HTTP
                    requestEntity, // Corpo della richiesta
                    String.class // Tipo della risposta attesa
            );

            System.out.println("Purchase API Response: " + response.getStatusCode()); // Log dello stato della risposta
        } catch (Exception e) {
            System.err.println("Error in Purchase API Call: " + e.getMessage()); // Log degli errori
        }
    }

    // Avvia la simulazione delle chiamate API
    public void startSimulation(long duration, TimeUnit unit) {
        System.out.println("Starting API call simulation...");

        // Pianifica l'arresto della simulazione dopo una durata specificata
        scheduler.schedule(this::stopSimulation, duration, unit);

        // Pianifica le chiamate API a intervalli casuali
        scheduler.scheduleAtFixedRate(this::simulateCatalogSearch, 0, randomInterval(), TimeUnit.SECONDS); // Ricerca nel catalogo
        scheduler.scheduleAtFixedRate(this::simulatePurchase, 0, randomInterval(), TimeUnit.SECONDS); // Acquisto
    }

    // Arresta la simulazione delle chiamate API
    public void stopSimulation() {
        System.out.println("Stopping API call simulation...");
        scheduler.shutdown(); // Arresta l'esecutore
    }

}
