package com.microservices.shopgateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Annota la classe come un controller REST
public class FallbackController {

    // Questo endpoint viene invocato quando il servizio catalogo non è disponibile
    @GetMapping(value = "/fallback-catalog", produces = "application/json")
    public String fallbackCatalog() {
        // Ritorna un messaggio in formato JSON che informa l'utente dell'indisponibilità del servizio
        return "{\"message\":\"We regret to inform service catalog is currently unavailable. please try again later\"}";
    }
}
