package com.microservice.ConfigurationClient.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Indica che questa classe è un controller REST
@RefreshScope // Permette il ricaricamento dinamico delle proprietà configurate tramite il server di configurazione
public class TestController {

    // Valore della proprietà `user.role` dal server di configurazione; default "guest" se non definito
    @Value("${user.role:guest}")
    private String role;

    // Valore della proprietà `user.password` dal server di configurazione
    @Value("${user.password}")
    private String password;

    @GetMapping("/role") // Mappa la richiesta GET sull'endpoint "/role"
    public String getRole() {
        // Restituisce un messaggio che include il ruolo e la password letti dalla configurazione
        return String.format("Hello! I am a %s and my password is %s ", role, password);
    }
}
